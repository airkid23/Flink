package cn.yourdad;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @program: FlinkConnectorDemo
 * @package: PACKAGE_NAME
 * @description:
 * @author: wzj
 * @create: 2020-12-19 14:22
 **/

public class connectorTest {

    public static void main(String[] args) {

        EnvironmentSettings fsSetting = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        TableEnvironment tableEnv = StreamTableEnvironment.create(env, fsSetting);


        try {
            tableEnv.executeSql("CREATE TABLE UserScores (name STRING, score INT)\n" +
                    "WITH (\n" +
                    "  'connector' = 'socket',\n" +
                    "  'hostname' = 'localhost',\n" +
                    "  'port' = '9999',\n" +
                    "  'byte-delimiter' = '10',\n" +
                    "  'format' = 'changelog-csv',\n" +
                    "  'changelog-csv.column-delimiter' = '|'\n" +
                    ")");

            tableEnv.executeSql("CREATE TABLE UserSink (name STRING, score INT)\n" +
                    "WITH (\n" +
                    "  'connector' = 'print')");

            tableEnv.executeSql("insert into UserSink select * from UserScores");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
