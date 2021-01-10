package com.akryvtsun.populus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkHolder {
    public int status;
    public Map<Long, Link> list;
    public Object error;
}
