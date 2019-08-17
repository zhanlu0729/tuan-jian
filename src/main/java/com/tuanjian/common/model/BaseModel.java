package com.tuanjian.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 5773844055158490786L;

    private static final String DEAULT_USER = "BONE-ADMIN";

    protected Long id;
    protected Date gmtCreate;
    protected String creator;
    protected Date gmtModified;
    protected String modifier;

    protected String orderBy;
    protected Integer limit;

    protected transient Map<String, Object> fieldExt = new HashMap<>();

}
