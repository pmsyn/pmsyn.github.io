package com.shiro.pojo;

/**
 * @author:pms
 * @createtime:2018/7/12-16:30
 * @qq 718195578
 * @since 1.0
 */
public class TableField {
    private String columnName;
    private String dataType;
    private int dataLength;
    private int nullable;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public int getNullable() {
        return nullable;
    }

    public void setNullable(int nullable) {
        this.nullable = nullable;
    }
}
