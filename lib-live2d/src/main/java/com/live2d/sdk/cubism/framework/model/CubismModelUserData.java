/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.model;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.id.CubismId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is a manager of user data. It can load, manage user data.
 */
public class CubismModelUserData {
    /**
     * Class for recording the user data read from JSON.
     */
    public static class CubismModelUserDataNode {
        /**
         * Constructor
         *
         * @param targetType user data target type
         * @param targetId ID of user data target
         * @param value user data
         */
        public CubismModelUserDataNode(
            CubismId targetType,
            CubismId targetId,
            String value
        ) {
            if (value == null) {
                throw new IllegalArgumentException("value is null.");
            }
            this.targetType = new CubismId(targetType);
            this.targetId = new CubismId(targetId);
            this.value = value;
        }

        /**
         * User data target type
         */
        public final CubismId targetType;
        /**
         * User data target ID
         */
        public final CubismId targetId;
        /**
         * User data
         */
        public final String value;
    }

    /**
     * Create an instance.
     *
     * @param buffer a buffer where userdata3.json is loaded.
     * @return the created instance. If parsing JSON data failed, return null.
     */
    public static CubismModelUserData create(byte[] buffer) {
        CubismModelUserData modelUserData = new CubismModelUserData();
        boolean isSuccessful = modelUserData.parseUserData(buffer);

        if (isSuccessful) {
            return modelUserData;
        }
        return null;
    }

    /**
     * Get the user data list of ArtMesh.
     *
     * @return the user data list
     */
    public List<CubismModelUserDataNode> getArtMeshUserData() {
        if (areArtMeshUserDataNodesChanged) {
            cachedImmutableArtMeshUserDataNodes = Collections.unmodifiableList(artMeshUserDataNodes);
            areArtMeshUserDataNodesChanged = false;
        }
        return cachedImmutableArtMeshUserDataNodes;
    }

    /**
     * Get the user data of ArtMesh.
     *
     * @param index index of data to be obtained
     * @return CubismModelUserDataNode instance
     */
    public CubismModelUserDataNode getArtMeshUserData(int index) {
        return artMeshUserDataNodes.get(index);
    }

    /**
     * ID name "ArtMesh"
     */
    private static final String ART_MESH = "ArtMesh";

    /**
     * Parse a userdata3.json data.
     *
     * @param buffer a buffer where userdata3.json is loaded.
     * @return If parsing userdata3.json is successful, return true.
     */
    private boolean parseUserData(byte[] buffer) {
        CubismModelUserDataJson userdata3Json;
        userdata3Json = new CubismModelUserDataJson(buffer);

        final CubismId artMeshType = CubismFramework.getIdManager().getId(ART_MESH);
        final int nodeCount = userdata3Json.getUserDataCount();

        for (int i = 0; i < nodeCount; i++) {
            final CubismId targetType = CubismFramework.getIdManager().getId(userdata3Json.getUserDataTargetType(i));
            final CubismId targetId = userdata3Json.getUserDataId(i);
            final String value = userdata3Json.getUserDataValue(i);
            final CubismModelUserDataNode addedNode = new CubismModelUserDataNode(
                targetType,
                targetId,
                value
            );
            userDataNodes.add(addedNode);

            if (addedNode.targetType.equals(artMeshType)) {
                artMeshUserDataNodes.add(addedNode);
            }
        }

        return true;
    }

    /**
     * the list which has a user data struct class
     */
    private final List<CubismModelUserDataNode> userDataNodes = new ArrayList<CubismModelUserDataNode>();
    /**
     * 閲覧リスト保持
     */
    private final List<CubismModelUserDataNode> artMeshUserDataNodes = new ArrayList<CubismModelUserDataNode>();

    private boolean areArtMeshUserDataNodesChanged = true;

    private List<CubismModelUserDataNode> cachedImmutableArtMeshUserDataNodes;
}
