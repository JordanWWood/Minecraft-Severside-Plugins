package network.marble.dataaccesslayer.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import network.marble.dataaccesslayer.common.Context;
import network.marble.dataaccesslayer.common.ModelUtils;
import network.marble.dataaccesslayer.entities.Result;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.CacheManager;
import network.marble.dataaccesslayer.managers.MetricsManager;
import okhttp3.Request;
import org.influxdb.dto.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseModel<T extends BaseModel> implements IBaseModel{

    @JsonIgnore
    protected ExecutorService executor = Executors.newFixedThreadPool(10);

    @JsonIgnore
    protected final String urlEndPoint;

    @JsonIgnore
    protected final Context context;

    public BaseModel(String urlEndPoint) {
        this.urlEndPoint = urlEndPoint;
        this.context = Context.getInstance();
    }

    @Getter @Setter
    public UUID id;

    public boolean exists() {
        return getId() != null;
    }

    @SuppressWarnings("unchecked")
    public Future<T> getAsync(UUID id) {
        return executor.submit(() -> this.get(id));
    }

    @Deprecated
    public T get(UUID id) throws APIException {
        //if (id == null) throw new NullArgumentException("id");
        if (CacheManager.enabled && CacheManager.getInstance().containsKey(id)) return CacheManager.getInstance().get(id, getTypeClass());
        T item = getFromURL(urlEndPoint+"/"+id.toString());
        if (item.exists() && CacheManager.enabled) CacheManager.getInstance().put(id, item);
        return item;
    }

    public Future<List<T>> getAsync(List<UUID> ids) {
        return executor.submit(() -> this.get(ids));
    }

    @Deprecated
    public List<T> get(List<UUID> ids) throws APIException {
        //if (ids == null) throw new NullArgumentException("ids");
        List<UUID> nonCachedIds;
        List<T> cachedEntries;
        final CacheManager cache = CacheManager.getInstance();
        if (CacheManager.enabled) {

            List<UUID> cachedIds;

            nonCachedIds = ids.stream().filter((id) -> !cache.containsKey(id)).collect(Collectors.toList());
            cachedIds = ids.stream().filter(cache::containsKey).collect(Collectors.toList());
            cachedEntries = cache.getAll(cachedIds, getTypeClass());
        } else {
            nonCachedIds = ids;
            cachedEntries = new ArrayList<>();
        }

        String sIds = String.join(",", nonCachedIds.stream().map(Object::toString).collect(Collectors.toList()));
        List<T> nonCachedEntries = getsFromURL(urlEndPoint+"/"+sIds);

        if (nonCachedEntries.size() > 0) {
            for (T item: nonCachedEntries.stream().filter(BaseModel::exists).collect(Collectors.toList())) {
                cache.put(item.getId(), item);
            }
        }

        nonCachedEntries.addAll(cachedEntries);
        return nonCachedEntries;
    }

    public Future<List<T>> getAsync() {
        return executor.submit((Callable<List<T>>) this::get);
    }

    @Deprecated
    public List<T> get() throws APIException {
        return getsFromURL(urlEndPoint);
    }

    protected T getFromURL(String url) throws APIException {
        long time = System.currentTimeMillis();

        Request r = context.getRequest(url);
        String data = context.executeRequest(r);
        ModelUtils.errorCheck(data);
        T item = deserializeModel(data);

        roundTrip(System.currentTimeMillis() - time, "get");
        return item;
    }

    protected List<T> getsFromURL(String url) throws APIException {
        long time = System.currentTimeMillis();

        Request r = context.getRequest(url);
        String data = context.executeRequest(r);
        ModelUtils.errorCheck(data);
        if (data == null) return new ArrayList<>();
        List<T> models = deserializeModels(data);

        roundTrip(System.currentTimeMillis() - time, "get");
        return models;
    }

    public Future<Boolean> saveAsync() {
        return executor.submit(this::save);
    }

    @Deprecated
    public boolean save() throws APIException {
        return this.exists() ? update() : insert();
    }

    public Future<Result> saveWithResultAsync() {
        return executor.submit(this::saveWithResult);
    }

    @Deprecated
    public Result saveWithResult() throws APIException {
        return this.exists() ? updateWithResult() : insertWithResult();
    }

    public Future<T> saveAndReturnAsync() {
        return executor.submit(this::saveAndReturn);
    }

    @Deprecated
    public T saveAndReturn() throws APIException {
        return this.exists() ? updateAndReturn() : insertAndReturn();
    }

    public Future<Boolean> updateAsync() {
        return executor.submit(this::update);
    }

    @Deprecated
    public boolean update() throws APIException {
        return _update(urlEndPoint + "/" + this.getId().toString()).success;
    }

    public Future<Result> updateWithResultAsync() {
        return executor.submit(this::updateWithResult);
    }

    @Deprecated
    public Result updateWithResult() throws APIException {
        return _update(urlEndPoint + "/" + this.getId().toString()).result;
    }

    public Future<T> updateAndReturnAsync() {
        return executor.submit(this::updateAndReturn);
    }

    @Deprecated
    public T updateAndReturn() throws APIException {
        return _update(urlEndPoint + "/" + this.getId().toString()).model;
    }

    @SuppressWarnings("unchecked")
    protected ReturnOptions<T> _update(String url) throws APIException {
        long time = System.currentTimeMillis();

        String json = serializeModel((T)this);
        Request r = context.putRequest(url, json);
        String returned = context.executeRequest(r);
        ModelUtils.errorCheck(returned);
        Result resultModel = deserializeModel(returned, Result.class);
        boolean result = resultModel != null && resultModel.getReplaced() > 0;
        if (result && CacheManager.getInstance().containsKey(this.getId()) && CacheManager.enabled) CacheManager.getInstance().replace(this.getId(), this);
        ReturnOptions<T> returnOptions = new ReturnOptions(resultModel, result, this);

        roundTrip(System.currentTimeMillis() - time, "update");
        return returnOptions;
    }

    public Future<Boolean> insertAsync() {
        return executor.submit(this::insert);
    }

    @Deprecated
    public boolean insert() throws APIException {
        return _insert(urlEndPoint).success;
    }

    public Future<Result> insertWithResultAsync() {
        return executor.submit(this::insertWithResult);
    }

    @Deprecated
    public Result insertWithResult() throws APIException {
        return _insert(urlEndPoint).result;
    }

    public Future<T> insertAndReturnAsync() {
        return executor.submit(this::insertAndReturn);
    }

    @Deprecated
    public T insertAndReturn() throws APIException {
        return _insert(urlEndPoint).model;
    }

    @SuppressWarnings("unchecked")
    protected ReturnOptions<T> _insert(String url) throws APIException {
        long time = System.currentTimeMillis();

        String json = serializeModel((T)this);
        Request r = context.postRequest(url, json);
        String returned = context.executeRequest(r);
        ModelUtils.errorCheck(returned);
        Result resultModel = deserializeModel(returned, Result.class);
        boolean result = resultModel != null && resultModel.getInserted() > 0;
        if (result && resultModel.getGeneratedKeys() != null && resultModel.getGeneratedKeys().size() > 0) {
            this.id = resultModel.getGeneratedKeys().get(0);
            if (CacheManager.enabled) CacheManager.getInstance().put(this.id, this);
        }
        ReturnOptions<T> returnOptions = new ReturnOptions(resultModel, result, this);

        roundTrip(System.currentTimeMillis() - time, "insert");
        return returnOptions;
    }

    public Future<Boolean> deleteAsync() {
        return executor.submit(this::delete);
    }

    @Deprecated
    public boolean delete() throws APIException {
        return _delete(urlEndPoint + "/" + this.getId().toString()).success;
    }

    public Future<Result> deleteWithResultAsync() {
        return executor.submit(this::deleteWithResult);
    }

    @Deprecated
    public Result deleteWithResult() throws APIException {
        return _delete(urlEndPoint + "/" + this.getId().toString()).result;
    }

    @SuppressWarnings("unchecked")
    protected ReturnOptions<T> _delete(String url) throws APIException {
        long time = System.currentTimeMillis();

        Request r = context.deleteRequest(url);
        String returned = context.executeRequest(r);
        ModelUtils.errorCheck(returned);
        Result resultModel = deserializeModel(returned, Result.class);
        boolean result = resultModel != null && resultModel.getDeleted() > 0;
        if (result && CacheManager.getInstance().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().remove(this.id);
        ReturnOptions<T> returnOptions = new ReturnOptions(resultModel, result, this);

        roundTrip(System.currentTimeMillis() - time, "delete");
        return returnOptions;
    }

    public abstract Class<?> getTypeClass();

    private <E> E _deserializeModel(String data, JavaType type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected T deserializeModel(String data) {
        return deserializeModel(data, getTypeClass());
    }

    protected <E> E deserializeModel(String data, Class<?> clazz) {
        if (data == null || data.equals("null")) data = "{}";
        JavaType type = new ObjectMapper().getTypeFactory().constructType(clazz);
        return _deserializeModel(data, type);
    }

    protected List<T> deserializeModels(String data) {
        return deserializeModels(data, getTypeClass());
    }

    protected <E> List<E> deserializeModels(String data, Class<?> clazz) {
        if (data == null || data.equals("null")) data = "[]";
        JavaType type = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, clazz);
        return _deserializeModel(data, type);
    }

    private <E> String _serializeModel(E data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String serializeModel(T data){
        return _serializeModel(data);
    }

    protected <E> String serializeModel(E data){
        return this._serializeModel(data);
    }

    protected String serializeModels(List<T> data){
        return this._serializeModel(data);
    }

    @SuppressWarnings("unchecked")
    public String toJSON() {
        return serializeModel((T)this);
    }

    private void roundTrip(Long time, String type) {
        Point trip = Point.measurement("api")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("type", type).tag("table", urlEndPoint)
                .addField("round_trip", time)
                .build();

        MetricsManager.getInstance().writePoint("Minevibe", "autogen", trip);
    }
}