/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.id;

/**
 * The name of parameters, parts and Drawaable is held in this class.
 */
public class CubismId {
    /**
     * Consturctor
     *
     * @param id A ID name
     * @throws IllegalArgumentException if an argument is null
     */
    public CubismId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null.");
        }
        this.id = id;
    }

    /**
     * Copy constructor
     *
     * @param id the CubismId instance
     */
    public CubismId(CubismId id) {
        this.id = id.getString();
    }

    /**
     * Get ID name
     */
    public String getString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubismId cubismId = (CubismId) o;

        return id != null ? id.equals(cubismId.id) : cubismId.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * ID name
     */
    private final String id;
}
