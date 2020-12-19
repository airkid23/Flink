package cn.yourdad; /**
 * @program: FlinkConnectorDemo
 * @package: PACKAGE_NAME
 * @description:
 * @author: wzj
 * @create: 2020-12-19 14:11
 **/

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.DynamicTableSource.DataStructureConverter;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.types.RowKind;

import java.util.List;

public class ChangelogCsvFormat implements DecodingFormat<DeserializationSchema<RowData>> {

    private final String columnDelimiter;

    public ChangelogCsvFormat(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    @SuppressWarnings("unchecked")
    public DeserializationSchema<RowData> createRuntimeDecoder(
            DynamicTableSource.Context context,
            DataType producedDataType) {
        // create type information for the DeserializationSchema
        final TypeInformation<RowData> producedTypeInfo = (TypeInformation<RowData>) context.createTypeInformation(
                producedDataType);

        // most of the code in DeserializationSchema will not work on internal data structures
        // create a converter for conversion at the end
        final DataStructureConverter converter = context.createDataStructureConverter(producedDataType);

        // use logical types during runtime for parsing
        final List<LogicalType> parsingTypes = producedDataType.getLogicalType().getChildren();

        // create runtime class
        return new ChangelogCsvDeserializer(parsingTypes, converter, producedTypeInfo, columnDelimiter);
    }

    public ChangelogMode getChangelogMode() {
        // define that this format can produce INSERT and DELETE rows
        return ChangelogMode.newBuilder()
                .addContainedKind(RowKind.INSERT)
                .addContainedKind(RowKind.DELETE)
                .build();
    }
}