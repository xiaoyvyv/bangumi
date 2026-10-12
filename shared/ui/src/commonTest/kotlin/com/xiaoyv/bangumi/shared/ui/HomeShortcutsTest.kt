package com.xiaoyv.bangumi.shared.ui

import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeShortcutsTest {
    @Test
    fun noSavedOrderKeepsAllDefaultShortcuts() {
        assertEquals(TabTokens.mainHomeActions, TabTokens.orderedHomeActions(emptyList()))
    }

    @Test
    fun savedOrderPreservesChoicesAndAppendsNewShortcuts() {
        val defaults = TabTokens.mainHomeActions
        val savedIds = listOf(defaults.last().type.toString(), defaults.first().type.toString())
        val reordered = TabTokens.orderedHomeActions(savedIds)
        assertEquals(listOf(defaults.last(), defaults.first()), reordered.take(2))
        assertEquals(defaults.size, reordered.size)
        assertEquals(defaults.toSet(), reordered.toSet())
    }

    @Test
    fun duplicateOrRemovedSavedIdsNeverDuplicateVisibleShortcuts() {
        val first = TabTokens.mainHomeActions.first()
        val reordered = TabTokens.orderedHomeActions(listOf("removed", first.type.toString(), first.type.toString()))
        assertEquals(TabTokens.mainHomeActions, reordered)
    }
}
