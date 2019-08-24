package network.marble.dataaccesslayer.models.base;

import lombok.AllArgsConstructor;
import network.marble.dataaccesslayer.entities.Result;

@AllArgsConstructor
public class ReturnOptions<T> {
    public Result result;
    public boolean success;
    public T model;
}
