/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.id;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class of ID names
 */
public class CubismIdManager {
    /**
     * Register IDs from list
     *
     * @param ids id list
     */
    public void registerIds(List<String> ids) {
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            registerId(id);
        }
    }

    /**
     * Register the specified number of IDs from the given list.
     *
     * @param ids ID list
     * @param count number of IDs to be registered
     */
    public void registerIds(List<String> ids, int count) {
        for (int i = 0; i < count; i++) {
            registerId(ids.get(i));
        }
    }

    /**
     * Register ID name
     *
     * @param id ID name
     * @return ID instance
     */
    public CubismId registerId(String id) {
        CubismId foundId = findId(id);

        if (foundId != null) {
            return foundId;
        }

        CubismId cubismId = new CubismId(id);
        ids.add(cubismId);

        return cubismId;
    }

    /**
     * Register ID.
     *
     * @param id ID instance
     * @return ID instance
     */
    public CubismId registerId(CubismId id) {
        return registerId(id.getString());
    }

    /**
     * Get ID from ID name.
     * If the given ID has not registered, register the ID, too.
     *
     * @param id ID name
     * @return ID instance
     */
    public CubismId getId(String id) {
        return registerId(id);
    }

    /**
     * Get ID from ID instance.
     * If the given ID has not registered, register the ID, too.
     *
     * @param id ID instance
     * @return ID instance
     */
    public CubismId getId(CubismId id) {
        return registerId(id);
    }

    /**
     * Check whether the ID has been already registered from an ID name.
     *
     * @return If given ID has been already registered, return true
     */
    public boolean isExist(String id) {
        return findId(id) != null;
    }

    public boolean isExist(CubismId id) {
        return findId(id) != null;
    }

    /**
     * Search an ID from given ID name.
     *
     * @param foundId ID name
     * @return If there is a registered ID, return the CubismId instance.
     */
    private CubismId findId(String foundId) {
        for (int i = 0; i < ids.size(); i++) {
            CubismId id = ids.get(i);

            if (id.getString().equals(foundId)) {
                return id;
            }
        }
        return null;
    }

    /**
     * Search an ID from given ID instance.
     *
     * @param foundId ID instance
     * @return If there is a registered ID, return the CubismId instance.
     */
    private CubismId findId(CubismId foundId) {
        for (int i = 0; i < ids.size(); i++) {
            CubismId id = ids.get(i);

            if (id.equals(foundId)) {
                return id;
            }
        }
        return null;
    }

    /**
     * The registered IDs list.
     */
    private final List<CubismId> ids = new ArrayList<CubismId>();
}
