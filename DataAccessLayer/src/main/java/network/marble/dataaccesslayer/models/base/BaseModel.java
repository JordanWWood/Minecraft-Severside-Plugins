package network.marble.dataaccesslayer.models.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.common.Context;
import network.marble.dataaccesslayer.common.ModelUtils;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.CacheManager;
import okhttp3.Request;
import org.apache.commons.lang.NullArgumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public abstract class BaseModel<T extends BaseModel> {
    protected final String urlEndPoint;
    private final String multiName;
    private final String singleName;
    protected final Context context;
    private final ModelUtils<T> utils;

    public BaseModel(String urlEndPoint, String multiName, String singleName) {
        this.urlEndPoint = urlEndPoint;
        this.multiName = multiName;
        this.singleName = singleName;
        this.context = Context.getInstance();
        this.utils = new ModelUtils<T>();
    }

    @Getter @Setter
    public UUID id;

    public boolean exists() {
        return id != null;
    }

    /*
    @SuppressWarnings("unchecked")
	public CompletableFuture<T> get(UUID id) throws APIException {
        CompletableFuture<T> future = new CompletableFuture<T>();
        new Thread( () -> {
            if (CacheManager.enabled && CacheManager.getInstance().getCache().containsKey(id)) future.complete((T) CacheManager.getInstance().getCache().get(this.id));
            Request r = context.getRequest(urlEndPoint, id.toString());
            String data = context.executeRequest(r);
            try {
                utils.errorCheck(data);
            } catch (APIException e) {
                future.completeExceptionally(e);
            }
            data = utils.getJsonAtRoot(data, singleName);
            T item = data == null ? null : deserializeModel(data);
            if (item != null && CacheManager.enabled) CacheManager.getInstance().getCache().put(id, item);
            future.complete(item);
        }).start();
        return future;
    }
    */

    @SuppressWarnings("unchecked")
    public T get(UUID id) throws APIException {
        //if (id == null) throw new NullArgumentException("id");
        if (CacheManager.enabled && CacheManager.getInstance().getCache().containsKey(id)) return (T) CacheManager.getInstance().getCache().get(id);
        T item = getSingle(urlEndPoint+"/"+id.toString());
        if (item != null && CacheManager.enabled) CacheManager.getInstance().getCache().put(id, item);
        return item;
    }

    public List<T> get(List<UUID> ids) throws APIException {
        //if (ids == null) throw new NullArgumentException("ids");
        String sIds = String.join(",", ids.stream().map(Object::toString).collect(Collectors.toList()));
        return getMultiple(urlEndPoint+"/"+sIds);
    }

    public List<T> get() throws APIException {
        return getMultiple(urlEndPoint);
    }

    protected T getSingle(String url) throws APIException {
        Request r = context.getRequest(url);
        String data = context.executeRequest(r);
        utils.errorCheck(data);
        data = utils.getJsonAtRoot(data, singleName);
        T item = data == null ? null : deserializeModel(data);
        return item;
    }

    protected List<T> getMultiple(String url) throws APIException {
        Request r = context.getRequest(url);
        String data = context.executeRequest(r);
        utils.errorCheck(data);
        data = utils.getJsonAtRoot(data, multiName);
        if (data == null) return new ArrayList<T>();
        return deserializeModels(data);
    }

    public boolean save() throws APIException {
        return this.exists() ? update() : insert();
    }

    public boolean update() throws APIException {
        return update(urlEndPoint + "/" + this.id.toString());
    }

    @SuppressWarnings("unchecked")
    protected boolean update(String url) throws APIException {
        String json = serializeModel((T)this);
        Request r = context.putRequest(url, json);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        returned = utils.getJsonAtRoot(returned, "replaced");
        boolean result = returned != null && Integer.parseInt(returned) > 0;
        if (result && CacheManager.getInstance().getCache().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().getCache().remove(this.id);
        return result;
    }

    public boolean insert() throws APIException {
        return insert(urlEndPoint);
    }

    @SuppressWarnings("unchecked")
    protected boolean insert(String url) throws APIException {
        String json = serializeModel((T)this);
        Request r = context.postRequest(url, json);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        returned = utils.getJsonAtRoot(returned, "inserted");
        return returned != null && Integer.parseInt(returned) > 0;
    }

    public boolean delete() throws APIException {
        return insert(urlEndPoint + "/" + this.id.toString());
    }

    protected boolean delete(String url) throws APIException {
        Request r = context.deleteRequest(url);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        returned = utils.getJsonAtRoot(returned, "deleted");
        boolean result = returned != null && Integer.parseInt(returned) > 0;
        if (result && CacheManager.getInstance().getCache().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().getCache().remove(this.id);
        return result;
    }

    public abstract Class<?> getTypeClass();

    protected T deserializeModel(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JavaType type = mapper.getTypeFactory().constructType(getTypeClass());
            return mapper.readValue(data, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected List<T> deserializeModels(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, getTypeClass());
            return mapper.readValue(data, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String serializeModel(T data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String serializeModels(List<T> data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "id=" + id +
                '}';
    }
}