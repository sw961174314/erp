package com.ec.saas.dto;

import lombok.Data;

@Data
public class TenantDatabaseDTO {
    public String tenantDatabase;
    public String dbUser;
    public String dbPass;
    public String adminName;
    public String adminPass;
    public String tenantName;
    public String url;
}
