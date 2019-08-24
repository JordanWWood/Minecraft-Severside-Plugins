package network.marble.dataaccesslayer.managers;

import network.marble.dataaccesslayer.base.DataAccessLayer;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

/**
 * Time series data store for producing detailed metrics that can be represented in Chronograf or Grafana
 */
public class MetricsManager  {
    private static MetricsManager instance;

    private InfluxDB influxDB = InfluxDBFactory.connect(DataAccessLayer.getEnvironmentalVariable("INFLUX_ADDRESS"));

    public MetricsManager() {
        influxDB.enableBatch(BatchOptions.DEFAULTS.actions(10).flushDuration(100));
    }

    /**
     * Write the given point of data to the given db with the given retention policy name
     *
     * @param db the name of the db to be used
     * @param retention the name of the retention policy that should be used
     * @param point the point of data to be written
     */
    public void writePoint(String db, String retention, Point point) {
        influxDB.write(db, retention, point);
    }

    public static MetricsManager getInstance() {
        if (instance == null) instance = new MetricsManager();
        return instance;
    }
}
