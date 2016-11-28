package com.fashion.binge.fashiondesign.helper;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class FacebookConfiguration {
    public FacebookConfiguration() {

    }

    public SimpleFacebookConfiguration getConfiguration() {
        Permission[] permissions = new Permission[]{
                Permission.USER_PHOTOS,
                Permission.EMAIL,
                Permission.PUBLISH_ACTION,
                Permission.PUBLIC_PROFILE

        };
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("952504131513072")
                .setNamespace("binge")
                .setPermissions(permissions)
                .build();
        return configuration;
    }
}

