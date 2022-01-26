package com.pattycake.example.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class InputModel implements Serializable {

    @QuerySqlField(index = true)
    private String key;

    @QuerySqlField(index = true)
    private int value;

    @QuerySqlField
    private byte[] content;

}
