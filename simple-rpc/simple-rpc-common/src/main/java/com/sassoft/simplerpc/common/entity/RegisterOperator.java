package com.sassoft.simplerpc.common.entity;

import java.util.List;

/**
 * Created by shjk_000 on 2019/3/9.
 */
public class RegisterOperator {
    private String action;
    private RegisterEntity registerEntity;

    public List<RegisterEntity> getRegistedEntities() {
        return registedEntities;
    }

    public void setRegistedEntities(List<RegisterEntity> registedEntities) {
        this.registedEntities = registedEntities;
    }

    private List<RegisterEntity> registedEntities;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public RegisterEntity getRegisterEntity() {
        return registerEntity;
    }

    public void setRegisterEntity(RegisterEntity registerEntity) {
        this.registerEntity = registerEntity;
    }
}
