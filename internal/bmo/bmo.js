;(function (global) {
    'use strict';

    var CODE_PREFIX = 'bmo';
    var DEFAULT_WIDTH = 63;
    var DEFAULT_HEIGHT = 63;
    var DEFAULT_SELECTOR = '.bmo[data-code]';
    var DEFAULT_MANIFEST_URL = 'assets/manifest.local.json';
    var DEFAULT_ASSTET_PATH = '/js/lib/bmo/';
    var SAVED_BMO_STORAGE_KEY = 'chii_saved_bmo';
    var SAVED_BMO_UPDATED_KEY = 'chii_saved_bmo_updated';

    var state = {
        assets: null,
        categories: {},
        itemIndex: new Map(),
        loadPromises: new Map(),
        compactIndex: new Map(),
        compactReverse: [],
        renderCache: new Map(),
        options: {
            width: DEFAULT_WIDTH,
            height: DEFAULT_HEIGHT,
            selector: DEFAULT_SELECTOR,
            devicePixelRatio: 1
        },
        renderCacheLimit: 200,
        observer: null,
        manifestAttempted: false,
        defaultManifestUrl: DEFAULT_ASSTET_PATH + DEFAULT_MANIFEST_URL
    };

    function makeRenderCacheKey(code, width, height) {
        var normalizedCode = code ? String(code).trim() : '';
        var w = Number(width) || 0;
        var h = Number(height) || 0;
        return normalizedCode + '::' + w + 'x' + h;
    }

    function getCachedRender(key) {
        if (!key || !state.renderCache) {
            return null;
        }
        var cached = state.renderCache.get(key) || null;
        if (!cached) {
            return null;
        }
        // Simple LRU: move entry to the end on hit
        state.renderCache.delete(key);
        state.renderCache.set(key, cached);
        return cached;
    }

    function setRenderCache(key, entry) {
        if (!key || !entry || !state.renderCache) {
            return;
        }
        state.renderCache.set(key, entry);
        if (state.renderCache.size > state.renderCacheLimit) {
            var oldestKey = state.renderCache.keys().next().value;
            if (oldestKey !== undefined) {
                state.renderCache.delete(oldestKey);
            }
        }
    }

    function createImageFromDataUrl(dataUrl) {
        return new Promise(function (resolve, reject) {
            if (!dataUrl) {
                reject(new Error('Missing data URL.'));
                return;
            }
            var ImageCtor = global && global.Image ? global.Image : null;
            if (!ImageCtor) {
                reject(new Error('Image constructor is not available.'));
                return;
            }
            var image = new ImageCtor();
            image.onload = function () {
                resolve(image);
            };
            image.onerror = function (err) {
                reject(err);
            };
            image.src = dataUrl;
        });
    }

    function clearRenderCache() {
        if (state.renderCache) {
            state.renderCache.clear();
        }
    }

    function getSafeLocalStorage() {
        try {
            if (global && global.localStorage) {
                return global.localStorage;
            }
        } catch (err) {
            // ignore access errors (e.g., privacy mode)
        }
        return null;
    }

    function generateSavedBmoId() {
        var randomSuffix = Math.floor(Math.random() * 0xffffff).toString(36);
        return 'bmo_' + Date.now().toString(36) + '_' + randomSuffix;
    }

    function normalizeSavedBmoEntry(raw, options) {
        var entry = raw && typeof raw === 'object' ? raw : {};
        var normalizedName = entry.name !== undefined && entry.name !== null ? String(entry.name).trim() : '';
        var normalizedNote = entry.note !== undefined && entry.note !== null ? String(entry.note).trim() : (entry.description !== undefined && entry.description !== null ? String(entry.description).trim() : '');
        var normalized = {
            id: entry.id !== undefined && entry.id !== null && entry.id !== '' ? String(entry.id) : null,
            code: entry.code !== undefined && entry.code !== null ? String(entry.code).trim() : '',
            name: normalizedName,
            note: normalizedNote,
            tags: Array.isArray(entry.tags) ? entry.tags.slice(0, 20).map(function (tag) {
                return String(tag);
            }) : undefined,
            createdAt: typeof entry.createdAt === 'number' && isFinite(entry.createdAt) ? entry.createdAt : null,
            updatedAt: typeof entry.updatedAt === 'number' && isFinite(entry.updatedAt) ? entry.updatedAt : null
        };
        var opts = options || {};
        if (opts.ensureId && !normalized.id) {
            normalized.id = generateSavedBmoId();
        }
        if (opts.ensureTimestamps) {
            var now = Date.now();
            if (!normalized.createdAt) {
                normalized.createdAt = now;
            }
            normalized.updatedAt = now;
        }
        return normalized;
    }

    function dedupeSavedBmoEntries(entries) {
        if (!Array.isArray(entries) || !entries.length) {
            return [];
        }
        var seenIds = Object.create(null);
        var seenCodes = Object.create(null);
        var result = [];
        for (var i = 0; i < entries.length; i++) {
            var entry = entries[i];
            if (!entry || typeof entry !== 'object') {
                continue;
            }
            var id = entry.id !== undefined && entry.id !== null && entry.id !== '' ? String(entry.id) : null;
            var code = entry.code !== undefined && entry.code !== null && entry.code !== '' ? String(entry.code) : null;
            if (id && seenIds[id]) {
                continue;
            }
            if (!id && code && seenCodes[code]) {
                continue;
            }
            if (id) {
                seenIds[id] = true;
            }
            if (code) {
                seenCodes[code] = true;
            }
            result.push(entry);
        }
        return result;
    }

    function cloneSavedBmoEntries(entries) {
        if (!Array.isArray(entries) || !entries.length) {
            return [];
        }
        var clones = [];
        for (var i = 0; i < entries.length; i++) {
            var entry = entries[i];
            if (!entry || typeof entry !== 'object') {
                continue;
            }
            var clone;
            try {
                clone = JSON.parse(JSON.stringify(entry));
            } catch (err) {
                clone = Object.assign({}, entry);
            }
            if (!clone) {
                continue;
            }
            if (clone.meta !== undefined) {
                delete clone.meta;
            }
            if (clone.data !== undefined) {
                delete clone.data;
            }
            clones.push(clone);
        }
        return clones;
    }

    function readSavedBmoEntries() {
        var storage = getSafeLocalStorage();
        if (!storage) {
            return [];
        }
        var raw;
        try {
            raw = storage.getItem(SAVED_BMO_STORAGE_KEY);
        } catch (err) {
            return [];
        }
        if (!raw) {
            return [];
        }
        try {
            var parsed = JSON.parse(raw);
            if (!Array.isArray(parsed)) {
                return [];
            }
            var needsRewrite = false;
            var normalized = parsed.map(function (item) {
                if (item && typeof item === 'object') {
                    if (Object.prototype.hasOwnProperty.call(item, 'data') || Object.prototype.hasOwnProperty.call(item, 'meta')) {
                        needsRewrite = true;
                    }
                }
                return normalizeSavedBmoEntry(item || {}, { ensureId: true, ensureTimestamps: false });
            });
            var deduped = dedupeSavedBmoEntries(normalized);
            if (needsRewrite) {
                writeSavedBmoEntries(deduped);
            }
            return deduped;
        } catch (err) {
            return [];
        }
    }

    function writeSavedBmoEntries(entries) {
        var storage = getSafeLocalStorage();
        var list = Array.isArray(entries) ? entries : [];
        var normalized = list.map(function (item) {
            return normalizeSavedBmoEntry(item || {}, { ensureId: true, ensureTimestamps: false });
        });
        var deduped = dedupeSavedBmoEntries(normalized);
        if (!storage) {
            return deduped;
        }
        try {
            if (!deduped.length) {
                storage.removeItem(SAVED_BMO_STORAGE_KEY);
            } else {
                storage.setItem(SAVED_BMO_STORAGE_KEY, JSON.stringify(deduped));
            }
            storage.setItem(SAVED_BMO_UPDATED_KEY, String(Date.now()));
        } catch (err) {
            // ignore write errors
        }
        return deduped;
    }

    function listSavedBmoEntries() {
        return cloneSavedBmoEntries(readSavedBmoEntries());
    }

    function createSavedBmoEntry(data) {
        var pending = normalizeSavedBmoEntry(data || {}, { ensureId: true, ensureTimestamps: true });
        var existing = readSavedBmoEntries();
        var combined = [pending].concat(existing);
        var saved = writeSavedBmoEntries(combined);
        var stored = null;
        for (var i = 0; i < saved.length; i++) {
            if (saved[i] && saved[i].id === pending.id) {
                stored = saved[i];
                break;
            }
        }
        return stored ? cloneSavedBmoEntries([stored])[0] : null;
    }

    function updateSavedBmoEntry(id, updates, options) {
        if (!id) {
            return null;
        }
        var idStr = String(id);
        var patch = updates && typeof updates === 'object' ? updates : {};
        var existing = readSavedBmoEntries();
        var matchIndex = -1;
        for (var i = 0; i < existing.length; i++) {
            var candidate = existing[i];
            if (!candidate) {
                continue;
            }
            if (candidate.id === idStr) {
                matchIndex = i;
                break;
            }
        }
        var opts = options || {};
        if (matchIndex === -1 && opts.matchByCode) {
            for (var j = 0; j < existing.length; j++) {
                var entry = existing[j];
                if (entry && entry.code === idStr) {
                    matchIndex = j;
                    break;
                }
            }
        }
        if (matchIndex === -1) {
            return null;
        }
        var original = existing[matchIndex];
        var merged = Object.assign({}, original, patch || {});
        merged.id = original.id;
        if (!merged.createdAt && original.createdAt) {
            merged.createdAt = original.createdAt;
        }
        var normalized = normalizeSavedBmoEntry(merged, { ensureId: true, ensureTimestamps: true });
        if (original.createdAt) {
            normalized.createdAt = original.createdAt;
        }
        existing[matchIndex] = normalized;
        var saved = writeSavedBmoEntries(existing);
        var stored = null;
        for (var k = 0; k < saved.length; k++) {
            if (saved[k] && saved[k].id === normalized.id) {
                stored = saved[k];
                break;
            }
        }
        return stored ? cloneSavedBmoEntries([stored])[0] : null;
    }

    function deleteSavedBmoEntry(id, options) {
        if (!id) {
            return false;
        }
        var idStr = String(id);
        var opts = options || {};
        var allowCodeMatch = !!opts.matchByCode;
        var existing = readSavedBmoEntries();
        var filtered = [];
        var removed = false;
        for (var i = 0; i < existing.length; i++) {
            var entry = existing[i];
            if (!entry) {
                continue;
            }
            var matches = entry.id === idStr || (allowCodeMatch && entry.code === idStr);
            if (matches) {
                removed = true;
                continue;
            }
            filtered.push(entry);
        }
        if (!removed) {
            return false;
        }
        writeSavedBmoEntries(filtered);
        return true;
    }

    function clearSavedBmoEntries() {
        var storage = getSafeLocalStorage();
        if (storage) {
            try {
                storage.removeItem(SAVED_BMO_STORAGE_KEY);
                storage.setItem(SAVED_BMO_UPDATED_KEY, String(Date.now()));
            } catch (err) {
                // ignore
            }
        }
    }

    function getSavedBmoDisplayName(entry, index) {
        if (!entry) {
            return '';
        }
        if (entry.name) {
            return entry.name;
        }
        if (entry.note) {
            return entry.note;
        }
        if (entry.code) {
            var trimmed = String(entry.code).trim();
            if (trimmed.length <= 24) {
                return trimmed;
            }
            return trimmed.slice(0, 21) + '…';
        }
        if (typeof index === 'number') {
            return '收藏 #' + (index + 1);
        }
        return '收藏';
    }

    function formatSavedBmoTimestamp(value, options) {
        if (!value || !isFinite(value)) {
            return '';
        }
        var date = new Date(value);
        if (options && typeof options.format === 'function') {
            return options.format(date, value);
        }
        var pad = function (num) {
            return num < 10 ? '0' + num : String(num);
        };
        return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate()) + ' ' + pad(date.getHours()) + ':' + pad(date.getMinutes());
    }

    function addClassName(target, name) {
        if (!target || !name) {
            return;
        }
        if (target.classList) {
            target.classList.add(name);
            return;
        }
        var current = target.className || '';
        if (current.indexOf(name) === -1) {
            target.className = (current ? current + ' ' : '') + name;
        }
    }

    function createSavedBmoListRenderer(container, options) {
        if (!container) {
            return null;
        }
        var doc = container.ownerDocument || (global && global.document);
        if (!doc) {
            return null;
        }
        var config = options || {};
        var classNames = Object.assign({
            root: 'bmoji-saved-grid',
            card: 'bmoji-saved-card',
            preview: 'bmoji-saved-preview',
            body: 'bmoji-saved-body',
            title: 'bmoji-saved-title',
            meta: 'bmoji-saved-meta',
            note: 'bmoji-saved-note',
            actions: 'bmoji-saved-actions',
            actionButton: 'bmoji-saved-button',
            editButton: 'bmoji-saved-edit',
            deleteButton: 'bmoji-saved-delete',
            empty: 'bmoji-saved-empty'
        }, config.classNames || {});
        var previewOptions = Object.assign({
            width: state.options.width,
            height: state.options.height,
            devicePixelRatio: state.options.devicePixelRatio
        }, config.previewOptions || {});
        var getEntries = typeof config.getEntries === 'function' ? config.getEntries : listSavedBmoEntries;
        var beforeRender = typeof config.beforeRender === 'function' ? config.beforeRender : null;
        var afterRender = typeof config.afterRender === 'function' ? config.afterRender : null;
        var onSelect = typeof config.onSelect === 'function' ? config.onSelect : null;
        var onEdit = typeof config.onEdit === 'function' ? config.onEdit : null;
        var onDelete = typeof config.onDelete === 'function' ? config.onDelete : null;
        var titleFormatter = typeof config.titleFormatter === 'function' ? config.titleFormatter : getSavedBmoDisplayName;
        var metaFormatter = typeof config.metaFormatter === 'function' ? config.metaFormatter : function (entry) {
            var stamp = entry && (entry.updatedAt || entry.createdAt);
            if (!stamp) {
                return '';
            }
            return formatSavedBmoTimestamp(stamp, config.timestampOptions);
        };
        var emptyText = config.emptyText || '暂无收藏';
        var editActionLabel = config.editActionLabel || '编辑';
        var deleteActionLabel = config.deleteActionLabel || '删除';

        function render(entries) {
            var list = Array.isArray(entries) ? cloneSavedBmoEntries(entries) : getEntries();
            if (list && list.length > 1 && config.sort !== false) {
                list.sort(function (a, b) {
                    var aTime = (a && (a.updatedAt || a.createdAt)) || 0;
                    var bTime = (b && (b.updatedAt || b.createdAt)) || 0;
                    return bTime - aTime;
                });
            }
            if (beforeRender) {
                beforeRender(container, list);
            }
            container.innerHTML = '';
            if (classNames.root) {
                addClassName(container, classNames.root);
            }
            if (!list || !list.length) {
                var empty = doc.createElement('div');
                empty.className = classNames.empty;
                empty.textContent = emptyText;
                container.appendChild(empty);
                if (afterRender) {
                    afterRender(container, list);
                }
                return Promise.resolve([]);
            }

            var fragment = doc.createDocumentFragment();
            var renderPromises = [];

            for (var i = 0; i < list.length; i++) {
                var entry = list[i];
                if (!entry) {
                    continue;
                }
                var card = doc.createElement('div');
                card.className = classNames.card;
                if (entry.id) {
                    card.setAttribute('data-id', entry.id);
                }
                var preview = doc.createElement('div');
                preview.className = classNames.preview;
                if (entry.code) {
                    preview.setAttribute('data-code', entry.code);
                }
                card.appendChild(preview);

                var body = doc.createElement('div');
                body.className = classNames.body;
                var title = doc.createElement('div');
                title.className = classNames.title;
                title.textContent = titleFormatter(entry, i);
                body.appendChild(title);

                // var metaText = metaFormatter(entry, i);
                // if (metaText) {
                //     var metaNode = doc.createElement('div');
                //     metaNode.className = classNames.meta;
                //     metaNode.textContent = metaText;
                //     body.appendChild(metaNode);
                // }

                if (entry.note && entry.note !== entry.name) {
                    var noteNode = doc.createElement('div');
                    noteNode.className = classNames.note;
                    noteNode.textContent = entry.note;
                    body.appendChild(noteNode);
                }

                if (onEdit) {
                    (function (entryCopy, cardNode) {
                        var editBtn = doc.createElement('button');
                        editBtn.type = 'button';
                        editBtn.className = classNames.editButton;
                        editBtn.setAttribute('aria-label', editActionLabel);
                        editBtn.title = editActionLabel;
                        editBtn.textContent = typeof config.editButtonLabel === 'string' ? config.editButtonLabel : '✎';
                        editBtn.addEventListener('click', function (evt) {
                            evt.preventDefault();
                            evt.stopPropagation();
                            onEdit(entryCopy, evt, cardNode);
                        });
                        cardNode.appendChild(editBtn);
                    })(entry, card);
                }

                if (onDelete) {
                    (function (entryCopy, cardNode) {
                        var deleteBtn = doc.createElement('button');
                        deleteBtn.type = 'button';
                        deleteBtn.className = classNames.deleteButton;
                        deleteBtn.setAttribute('aria-label', deleteActionLabel);
                        deleteBtn.title = deleteActionLabel;
                        deleteBtn.textContent = typeof config.deleteButtonLabel === 'string' ? config.deleteButtonLabel : '×';
                        deleteBtn.addEventListener('click', function (evt) {
                            evt.preventDefault();
                            evt.stopPropagation();
                            onDelete(entryCopy, evt, cardNode);
                        });
                        cardNode.appendChild(deleteBtn);
                    })(entry, card);
                }

                card.appendChild(body);

                if (onSelect) {
                    (function (entryCopy, cardNode) {
                        cardNode.addEventListener('click', function (evt) {
                            if (evt.defaultPrevented) {
                                return;
                            }
                            onSelect(entryCopy, evt, cardNode);
                        });
                    })(entry, card);
                }

                fragment.appendChild(card);

                if (entry.code) {
                    try {
                        var renderPromise = renderElement(preview, Object.assign({}, previewOptions, { cache: false }));
                        renderPromises.push(Promise.resolve(renderPromise).catch(function () {
                            return null;
                        }));
                    } catch (err) {
                        renderPromises.push(Promise.resolve(null));
                    }
                }
            }

            container.appendChild(fragment);

            var wait = renderPromises.length ? Promise.all(renderPromises) : Promise.resolve([]);
            return wait.then(function (result) {
                if (afterRender) {
                    afterRender(container, list);
                }
                return result;
            });
        }

        return {
            container: container,
            options: config,
            render: render,
            refresh: function () {
                return render();
            }
        };
    }

    function tryLoadManifestFromUrl(url) {
        if (!url || typeof XMLHttpRequest === 'undefined') {
            return null;
        }
        try {
            var xhr = new XMLHttpRequest();
            xhr.open('GET', url, false);
            if (xhr.overrideMimeType) {
                xhr.overrideMimeType('application/json');
            }
            xhr.send(null);
            var status = xhr.status === 1223 ? 204 : xhr.status;
            if ((status >= 200 && status < 300) || status === 0) {
                var responseText = xhr.responseText;
                if (responseText) {
                    return JSON.parse(responseText);
                }
            }
        } catch (err) {
            if (global && global.console && console.warn) {
                console.warn('[bmoji] Failed to load manifest from', url, err);
            }
        }
        return null;
    }

    function toNumber(value) {
        if (value === null || value === undefined || value === '') {
            return null;
        }
        var num = Number(value);
        return isNaN(num) ? null : num;
    }

    function clamp(value, min, max) {
        return Math.min(Math.max(value, min), max);
    }

    function decodeModifierValue(value) {
        if (value === undefined) {
            return true;
        }
        if (value === 'true') {
            return true;
        }
        if (value === 'false') {
            return false;
        }
        var num = Number(value);
        if (!isNaN(num)) {
            return num;
        }
        return value;
    }

    function resolveCategoryMaxSelect(category) {
        if (!category || typeof category !== 'object') {
            return null;
        }
        if (typeof category.maxSelect === 'number') {
            var numeric = Math.floor(category.maxSelect);
            if (!isFinite(numeric) || numeric <= 0) {
                return null;
            }
            return numeric;
        }
        if (category.maxSelect === 0 || category.maxSelect === null) {
            return null;
        }
        if (category.multiSelect === false) {
            return 1;
        }
        if (category.multiSelect === true) {
            return null;
        }
        return 1;
    }

    function parseModifiers(raw) {
        var modifiers = {};
        if (!raw) {
            return modifiers;
        }
        var parts = raw.split('|');
        for (var i = 0; i < parts.length; i++) {
            var piece = parts[i];
            if (!piece) {
                continue;
            }
            var kv = piece.split('=');
            if (kv.length === 1) {
                modifiers[kv[0]] = true;
            } else {
                var key = kv.shift();
                var rest = kv.join('=');
                modifiers[key] = decodeModifierValue(rest);
            }
        }
        return modifiers;
    }

    function splitToken(raw) {
        if (!raw) {
            return { id: '', modifiers: {} };
        }
        var idx = raw.indexOf(':');
        if (idx === -1) {
            return { id: raw, modifiers: {} };
        }
        var id = raw.slice(0, idx);
        var modifierStr = raw.slice(idx + 1);
        return { id: id, modifiers: parseModifiers(modifierStr) };
    }

    var modifierAlias = {
        hue: 'h',
        lightness: 'l',
        saturation: 's',
        rotate: 'rotate',
        rotation: 'rotate',
        flipH: 'flipH',
        flipV: 'flipV',
        fliph: 'flipH',
        flipv: 'flipV',
        mirror: 'flipH',
        transform: 'tf',
        transformmask: 'tf',
        tf: 'tf',
        x: 'x',
        y: 'y',
        scale: 'scale',
        scaleX: 'scaleX',
        scaleY: 'scaleY',
        scaleH: 'scaleX',
        scaleV: 'scaleY'
    };

    function normalizeModifierKey(key) {
        if (!key) {
            return key;
        }
        var lower = key.toLowerCase();
        if (modifierAlias.hasOwnProperty(key)) {
            return modifierAlias[key];
        }
        if (modifierAlias.hasOwnProperty(lower)) {
            return modifierAlias[lower];
        }
        return key;
    }

    function formatModifierValue(value) {
        if (value === undefined) {
            return null;
        }
        if (typeof value === 'boolean') {
            return value ? true : 'false';
        }
        if (value === null) {
            return null;
        }
        if (typeof value === 'number') {
            if (Number.isInteger(value)) {
                return String(value);
            }
            return String(Number(value.toFixed(3)));
        }
        if (typeof value === 'string') {
            return value;
        }
        return null;
    }

    function ensureAssets() {
        if (state.assets) {
            return;
        }
        if (global && global.__BMOJI_ASSETS__) {
            setAssets(global.__BMOJI_ASSETS__);
            return;
        }
        if (!state.manifestAttempted) {
            state.manifestAttempted = true;
            var manifestUrl = (global && global.__BMOJI_MANIFEST_URL__) || state.defaultManifestUrl;
            var loaded = tryLoadManifestFromUrl(manifestUrl);
            if (!loaded && manifestUrl !== state.defaultManifestUrl) {
                loaded = tryLoadManifestFromUrl(state.defaultManifestUrl);
            }
            if (loaded) {
                setAssets(loaded);
                return;
            }
        }
        var manifestScript = global && global.document ? global.document.getElementById('bmoji-manifest') : null;
        if (manifestScript && manifestScript.textContent) {
            try {
                var parsed = JSON.parse(manifestScript.textContent);
                setAssets(parsed);
            } catch (err) {
                if (global && global.console && console.warn) {
                    console.warn('[bmoji] Failed to parse manifest JSON', err);
                }
            }
        }
    }

    function setAssets(assets) {
        state.assets = assets || {};
        state.categories = {};
        state.itemIndex = new Map();
        state.compactIndex = new Map();
        state.compactReverse = [];
        state.renderCache = new Map();
        state.manifestAttempted = true;
        if (global) {
            global.__BMOJI_ASSETS__ = state.assets;
        }

        if (!assets) {
            return;
        }

        var categories = Object.keys(assets);
        var versionBuckets = Object.create(null);
        var versionOrder = [];
        var nextCompactId = 0;

        function normalizeItemVersion(value) {
            var num = Number(value);
            if (!isFinite(num) || num <= 0) {
                return 1;
            }
            return Math.floor(num);
        }

        function addVersionEntry(version, entry) {
            var ver = normalizeItemVersion(version);
            if (!versionBuckets[ver]) {
                versionBuckets[ver] = [];
                versionOrder.push(ver);
            }
            versionBuckets[ver].push(entry);
        }

        for (var i = 0; i < categories.length; i++) {
            var key = categories[i];
            var category = assets[key];
            if (!category || !category.items) {
                continue;
            }
            var layerBase = typeof category.layer === 'number' ? category.layer : 0;
            var categoryCode = category && category.id ? String(category.id) : key;
            var maxSelect = resolveCategoryMaxSelect(category);
            if (category) {
                category.maxSelect = maxSelect;
                category.multiSelect = maxSelect === null || maxSelect > 1;
            }
            state.categories[key] = {
                key: key,
                id: categoryCode,
                code: categoryCode,
                name: category.name || key,
                layer: layerBase,
                multiSelect: maxSelect === null || maxSelect > 1,
                maxSelect: maxSelect
            };
            for (var j = 0; j < category.items.length; j++) {
                var item = category.items[j];
                if (!item || !item.id) {
                    continue;
                }
                var itemLayer = typeof item.layer === 'number' ? item.layer : layerBase;
                var itemId = String(item.id);
                var codeId = itemId;
                if (categoryCode && /^[0-9]+$/.test(itemId) && !item.custom) {
                    codeId = String(categoryCode) + itemId;
                }
                if (!item.codeId) {
                    item.codeId = codeId;
                }
                var version = normalizeItemVersion(item.version);
                var metaEntry = {
                    id: itemId,
                    codeId: codeId,
                    src: DEFAULT_ASSTET_PATH + item.src,
                    layer: itemLayer,
                    order: typeof item.order === 'number' && isFinite(item.order) ? item.order : j,
                    category: key,
                    categoryId: categoryCode,
                    alias: item.alias || null,
                    meta: item,
                    categoryMaxSelect: maxSelect,
                    version: version
                };
                state.itemIndex.set(itemId, metaEntry);
                if (codeId && !state.itemIndex.has(codeId)) {
                    state.itemIndex.set(codeId, metaEntry);
                }
                if (item.alias && !state.itemIndex.has(item.alias)) {
                    state.itemIndex.set(item.alias, metaEntry);
                }

                addVersionEntry(version, {
                    meta: metaEntry,
                    primaryKey: codeId,
                    itemId: itemId,
                    alias: item.alias ? String(item.alias) : null
                });
            }
        }

        versionOrder.sort(function (a, b) {
            return a - b;
        });

        for (var v = 0; v < versionOrder.length; v++) {
            var versionKey = versionOrder[v];
            var entries = versionBuckets[versionKey];
            if (!entries || !entries.length) {
                continue;
            }
            for (var idx = 0; idx < entries.length; idx++) {
                var entry = entries[idx];
                if (!entry || !entry.meta || !entry.primaryKey) {
                    continue;
                }
                if (!state.compactIndex.has(entry.primaryKey)) {
                    var compactId = nextCompactId++;
                    state.compactIndex.set(entry.primaryKey, compactId);
                    state.compactReverse[compactId] = entry.meta;
                    entry.meta.compactId = compactId;
                }
                var resolvedCompactId = state.compactIndex.get(entry.primaryKey);
                if (entry.itemId && !state.compactIndex.has(entry.itemId)) {
                    state.compactIndex.set(entry.itemId, resolvedCompactId);
                }
                if (entry.alias && !state.compactIndex.has(entry.alias)) {
                    state.compactIndex.set(entry.alias, resolvedCompactId);
                }
            }
        }
    }

    var COMPACT_FLAG_TF = 1;
    var COMPACT_FLAG_H = 2;
    var COMPACT_FLAG_L = 4;
    var COMPACT_FLAG_S = 8;
    var COMPACT_FLAG_X = 16;
    var COMPACT_FLAG_Y = 32;
    var COMPACT_FLAG_EXTRA = 64;

    function createVarWriter() {
        return {
            bytes: [],
            writeVarUint: function (value) {
                value = Number(value) >>> 0;
                while (value >= 128) {
                    this.bytes.push((value & 127) | 128);
                    value >>>= 7;
                }
                this.bytes.push(value & 127);
            },
            writeVarInt: function (value) {
                var encoded = encodeZigZag(value);
                this.writeVarUint(encoded);
            },
            writeByte: function (value) {
                this.bytes.push(Number(value) & 255);
            },
            writeBytes: function (bytes) {
                for (var i = 0; i < bytes.length; i++) {
                    this.writeByte(bytes[i]);
                }
            },
            finish: function () {
                return this.bytes;
            }
        };
    }

    function createVarReader(bytes) {
        return {
            bytes: bytes || [],
            offset: 0,
            readVarUint: function () {
                var result = 0;
                var shift = 0;
                while (this.offset < this.bytes.length) {
                    var byte = this.bytes[this.offset++];
                    result |= (byte & 127) << shift;
                    if ((byte & 128) === 0) {
                        return result >>> 0;
                    }
                    shift += 7;
                    if (shift > 35) {
                        return null;
                    }
                }
                return null;
            },
            readVarInt: function () {
                var encoded = this.readVarUint();
                if (encoded === null) {
                    return null;
                }
                return decodeZigZag(encoded);
            },
            readBytes: function (length) {
                if (this.offset + length > this.bytes.length) {
                    return null;
                }
                var out = this.bytes.slice(this.offset, this.offset + length);
                this.offset += length;
                return out;
            },
            hasMore: function () {
                return this.offset < this.bytes.length;
            }
        };
    }

    function encodeZigZag(value) {
        var num = Number(value) || 0;
        var intVal = num >= 0 ? Math.floor(num) : Math.ceil(num);
        return ((intVal << 1) ^ (intVal >> 31)) >>> 0;
    }

    function decodeZigZag(value) {
        var unsigned = Number(value) >>> 0;
        return (unsigned >>> 1) ^ -(unsigned & 1);
    }

    function encodeBase64Url(bytes) {
        if (!bytes || !bytes.length) {
            return '';
        }
        var binary = '';
        for (var i = 0; i < bytes.length; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        var base64;
        if (typeof global.btoa === 'function') {
            base64 = global.btoa(binary);
        } else if (typeof Buffer !== 'undefined') {
            base64 = Buffer.from(bytes).toString('base64');
        } else {
            var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
            var output = '';
            var block;
            for (var idx = 0; idx < binary.length; idx += 3) {
                var chunk = (binary.charCodeAt(idx) << 16) | ((idx + 1 < binary.length ? binary.charCodeAt(idx + 1) : 0) << 8) | (idx + 2 < binary.length ? binary.charCodeAt(idx + 2) : 0);
                var pad = idx + 2 >= binary.length ? (idx + 1 >= binary.length ? 2 : 1) : 0;
                for (var j = 18; j >= 0; j -= 6) {
                    block = (chunk >> j) & 63;
                    output += pad && j < pad * 6 ? '=' : chars.charAt(block);
                }
            }
            base64 = output;
        }
        return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
    }

    function decodeBase64Url(str) {
        if (!str) {
            return [];
        }
        var base64 = String(str).replace(/-/g, '+').replace(/_/g, '/');
        while (base64.length % 4) {
            base64 += '=';
        }
        var binary;
        if (typeof global.atob === 'function') {
            binary = global.atob(base64);
        } else if (typeof Buffer !== 'undefined') {
            var buf = Buffer.from(base64, 'base64');
            var out = [];
            for (var i = 0; i < buf.length; i++) {
                out.push(buf[i]);
            }
            return out;
        } else {
            var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
            binary = '';
            for (var i = 0; i < base64.length; i += 4) {
                var chunk = (chars.indexOf(base64.charAt(i)) << 18) |
                    (chars.indexOf(base64.charAt(i + 1)) << 12) |
                    (chars.indexOf(base64.charAt(i + 2)) << 6) |
                    chars.indexOf(base64.charAt(i + 3));
                binary += String.fromCharCode((chunk >> 16) & 255, (chunk >> 8) & 255, chunk & 255);
            }
        }
        var bytes = [];
        for (var idx = 0; idx < binary.length; idx++) {
            bytes.push(binary.charCodeAt(idx));
        }
        return bytes;
    }

    function utf8Encode(str) {
        if (!str) {
            return [];
        }
        if (typeof global.TextEncoder !== 'undefined') {
            return Array.prototype.slice.call(new global.TextEncoder().encode(str));
        }
        var utf8 = unescape(encodeURIComponent(str));
        var bytes = [];
        for (var i = 0; i < utf8.length; i++) {
            bytes.push(utf8.charCodeAt(i));
        }
        return bytes;
    }

    function utf8Decode(bytes) {
        if (!bytes || !bytes.length) {
            return '';
        }
        if (typeof global.TextDecoder !== 'undefined') {
            return new global.TextDecoder('utf-8').decode(new Uint8Array(bytes));
        }
        var str = '';
        for (var i = 0; i < bytes.length; i++) {
            str += String.fromCharCode(bytes[i]);
        }
        return decodeURIComponent(escape(str));
    }

    function isPlainObject(value) {
        if (!value || typeof value !== 'object') {
            return false;
        }
        return Object.getPrototypeOf(value) === Object.prototype || Object.getPrototypeOf(value) === null;
    }

    function consumeModifierKeys(consumed, modifiers, keys) {
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            if (Object.prototype.hasOwnProperty.call(modifiers, key)) {
                consumed[key] = true;
            }
        }
    }

    function extractSignedModifier(modifiers, keys) {
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            if (Object.prototype.hasOwnProperty.call(modifiers, key)) {
                var numeric = toNumber(modifiers[key]);
                if (numeric === null) {
                    return { present: true, encodable: false, key: key };
                }
                return { present: true, encodable: true, key: key, value: Math.round(numeric) };
            }
        }
        return { present: false, encodable: false };
    }	

    function decode(code) {
        if (!code) {
            return { raw: '', items: [], unknown: [], options: {} };
        }

        var trimmed = String(code).trim();
        if (!trimmed) {
            return { raw: '', items: [], unknown: [], options: {} };
        }

        if (trimmed.charAt(0) === '(' && trimmed.charAt(trimmed.length - 1) === ')') {
            trimmed = trimmed.slice(1, -1);
        }

        if (trimmed.indexOf('bmoC') === 0) {
            var compactParsed = decodeCompact(trimmed);
            if (compactParsed) {
                return compactParsed;
            }
        }

        var prefixUsed = CODE_PREFIX;

        if (!prefixUsed) {
            return { raw: trimmed, items: [], unknown: [trimmed], options: {} };
        }

        ensureAssets();

        var payload = trimmed.slice(prefixUsed.length);
        if (payload.charAt(0) === '_' || payload.charAt(0) === ':' || payload.charAt(0) === '-') {
            payload = payload.slice(1);
        }

        var scale = 1;
        var options = {};
        var parts = payload ? payload.split('_') : [];
        var resolved = [];
        var unknown = [];

        for (var index = 0; index < parts.length;) {
            var consumed = 0;
            var matched = null;
            var matchedModifiers = null;
            var maxSegments = parts.length - index;
            for (var probe = index; probe < parts.length; probe++) {
                if (parts[probe].indexOf(':') !== -1) {
                    maxSegments = probe - index + 1;
                    break;
                }
            }

            for (var len = maxSegments; len > 0; len--) {
                var chunk = parts.slice(index, index + len).join('_');
                var token = splitToken(chunk);
                var meta = state.itemIndex.get(token.id);
                if (meta) {
                    matched = meta;
                    matchedModifiers = token.modifiers;
                    consumed = len;
                    break;
                }
            }

            if (matched) {
                resolved.push({
                    id: matched.id,
                    src: matched.src,
                    layer: matched.layer,
                    order: matched.order,
                    category: matched.category,
                    modifiers: matchedModifiers || {},
                    meta: matched.meta
                });
                index += consumed;
                continue;
            }

            var part = parts[index];
            if (!part) {
                index += 1;
                continue;
            }

            if (part.indexOf('=') !== -1) {
                var pair = part.split('=');
                var key = pair[0];
                var value = pair.slice(1).join('=');
                options[key] = decodeModifierValue(value);
            } else if (part === 'x2' || part === 'x3' || part === 'x4') {
                scale = Number(part.slice(1));
                options.scale = scale;
            } else if (part.length) {
                unknown.push(part);
            }
            index += 1;
        }

        options.scale = options.scale || scale;
        resolved = enforceCategoryLimits(resolved);

        return {
            raw: trimmed,
            items: resolved,
            unknown: unknown,
            options: options
        };
    }

    function isModifierTruthy(value) {
        if (value === null || value === undefined) {
            return false;
        }
        if (typeof value === 'boolean') {
            return value;
        }
        if (typeof value === 'number') {
            return value !== 0;
        }
        if (typeof value === 'string') {
            var lower = value.toLowerCase();
            return lower === 'true' || lower === '1' || lower === 'yes' || lower === 'y';
        }
        return false;
    }

    function sanitizeTransformMask(maskValue) {
        var numeric = toNumber(maskValue);
        if (numeric === null) {
            return null;
        }
        var mask = Number(numeric) & 63;
        if (mask < 0) {
            mask = (mask + 64) & 63;
        }
        return mask;
    }

    function decodeTransformMask(maskValue) {
        var mask = sanitizeTransformMask(maskValue);
        if (mask === null) {
            return null;
        }
        return {
            mask: mask,
            flipH: !!(mask & 1),
            flipV: !!(mask & 2),
            rotation: ((mask >> 2) & 3) * 90
        };
    }

    function resolveTransformState(modifiers) {
        var decoded = decodeTransformMask(modifiers && modifiers.tf);
        if (decoded) {
            return decoded;
        }
        var rotationValue = toNumber(modifiers && modifiers.rotate);
        if (rotationValue === null) {
            rotationValue = toNumber(modifiers && modifiers.rotation);
        }
        return {
            mask: null,
            flipH: isModifierTruthy(modifiers && modifiers.flipH) || isModifierTruthy(modifiers && modifiers.fliph) || isModifierTruthy(modifiers && modifiers.mirror),
            flipV: isModifierTruthy(modifiers && modifiers.flipV) || isModifierTruthy(modifiers && modifiers.flipv),
            rotation: rotationValue || 0
        };
    }

    function computeTransformMask(modifiers) {
        if (!modifiers) {
            return { mask: null, implicit: false, encodedRotation: false };
        }
        var explicitMask = sanitizeTransformMask(modifiers.tf);
        if (explicitMask !== null) {
            return { mask: explicitMask, implicit: false, encodedRotation: true };
        }
        var flipH = isModifierTruthy(modifiers.flipH) || isModifierTruthy(modifiers.fliph) || isModifierTruthy(modifiers.mirror);
        var flipV = isModifierTruthy(modifiers.flipV) || isModifierTruthy(modifiers.flipv);
        var rawRotate = toNumber(modifiers.rotate);
        if (rawRotate === null) {
            rawRotate = toNumber(modifiers.rotation);
        }
        var normalizedRotate = 0;
        var rotationEncodable = false;
        if (rawRotate !== null) {
            var normalized = ((rawRotate % 360) + 360) % 360;
            if (normalized % 90 === 0) {
                normalizedRotate = normalized;
                rotationEncodable = normalized !== 0;
            }
        }
        if (!flipH && !flipV && (!rotationEncodable || normalizedRotate === 0)) {
            return { mask: null, implicit: false, encodedRotation: false };
        }
        var mask = 0;
        if (flipH) {
            mask |= 1;
        }
        if (flipV) {
            mask |= 2;
        }
        if (rotationEncodable) {
            var rotationIndex = Math.round(normalizedRotate / 90) & 3;
            mask |= (rotationIndex << 2);
        }
        return { mask: mask, implicit: true, encodedRotation: rotationEncodable && normalizedRotate !== 0 };
    }

    function encodeModifiers(modifiers) {
        if (!modifiers) {
            return '';
        }
        var entries = [];
        var transformInfo = computeTransformMask(modifiers);
        var mask = transformInfo.mask;
        var suppressFlipKeys = transformInfo.implicit;
        var suppressRotateKey = transformInfo.implicit && transformInfo.encodedRotation;
        var keys = Object.keys(modifiers);
        if (!keys.length) {
            if (mask !== null) {
                return 'tf=' + mask;
            }
            return '';
        }
        keys.sort();
        for (var i = 0; i < keys.length; i++) {
            var rawKey = keys[i];
            var normalizedKey = normalizeModifierKey(rawKey);
            if (!normalizedKey) {
                continue;
            }
            if (normalizedKey === 'tf') {
                // skip explicit tf entry; it will be appended once below if needed
                if (mask === null) {
                    var rawValueForTf = modifiers[rawKey];
                    var formattedTf = formatModifierValue(rawValueForTf);
                    if (formattedTf !== null) {
                        if (formattedTf === true) {
                            entries.push('tf');
                        } else {
                            entries.push('tf=' + formattedTf);
                        }
                    }
                }
                continue;
            }
            if (suppressFlipKeys && (normalizedKey === 'flipH' || normalizedKey === 'flipV')) {
                continue;
            }
            if (suppressRotateKey && normalizedKey === 'rotate') {
                continue;
            }
            var rawValue = modifiers[rawKey];
            var formatted = formatModifierValue(rawValue);
            if (formatted === null) {
                continue;
            }
            if (formatted === true) {
                entries.push(normalizedKey);
            } else {
                entries.push(normalizedKey + '=' + formatted);
            }
        }
        if (mask !== null) {
            entries.push('tf=' + mask);
        }
        return entries.join('|');
    }

    function encodeTokens(items, opts) {
        if (!items || !items.length) {
            return [];
        }
        var tokens = [];
        var seen = new Set();
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (!item) {
                continue;
            }
            var id = typeof item === 'string' ? item : item.id;
            if (!id) {
                continue;
            }
            if (seen.has(id) && (!opts || !opts.allowDuplicates)) {
                continue;
            }
            seen.add(id);
            var modifiers = item.modifiers || item.options || item.attrs || {};

            if (!modifiers || Object.keys(modifiers).length === 0) {
                tokens.push(id);
                continue;
            }

            var modifierString = encodeModifiers(modifiers);
            if (modifierString) {
                tokens.push(id + ':' + modifierString);
            } else {
                tokens.push(id);
            }
        }
        return tokens;
    }

    function extractModifiers(entry) {
        if (!entry || typeof entry !== 'object') {
            return {};
        }
        if (entry.modifiers && typeof entry.modifiers === 'object') {
            return entry.modifiers;
        }
        var collected = {};
        var candidateKeys = ['hue', 'lightness', 'saturation', 'h', 'l', 's', 'rotate', 'rotation', 'flipH', 'flipV', 'mirror', 'tf', 'x', 'y', 'scale', 'scaleX', 'scaleY', 'scaleH', 'scaleV'];
        for (var i = 0; i < candidateKeys.length; i++) {
            var key = candidateKeys[i];
            if (Object.prototype.hasOwnProperty.call(entry, key)) {
                collected[key] = entry[key];
            }
        }
        return collected;
    }

    function getCategoryLimit(categoryKey) {
        if (!categoryKey) {
            return null;
        }
        var categoryInfo = state.categories[categoryKey];
        if (!categoryInfo) {
            return null;
        }
        var limit = categoryInfo.maxSelect;
        if (typeof limit === 'number' && isFinite(limit) && limit > 0) {
            return limit;
        }
        return null;
    }

    function enforceCategoryLimits(items) {
        if (!items || !items.length) {
            return [];
        }
        var usage = Object.create(null);
        var limited = [];
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (!item) {
                continue;
            }
            var categoryKey = item.category || '';
            var limit = getCategoryLimit(categoryKey);
            if (limit !== null) {
                var count = usage[categoryKey] || 0;
                if (count >= limit) {
                    continue;
                }
                usage[categoryKey] = count + 1;
            }
            limited.push(item);
        }
        return limited;
    }

    function normalizeSelection(selection) {
        if (!selection) {
            return [];
        }
        var arr = [];
        if (Array.isArray(selection)) {
            arr = selection.slice();
        } else if (typeof selection === 'object') {
            var keys = Object.keys(selection);
            for (var i = 0; i < keys.length; i++) {
                var key = keys[i];
                var value = selection[key];
                if (value && typeof value === 'object') {
                    var clone = Object.assign({}, value);
                    clone.id = clone.id || key;
                    arr.push(clone);
                } else {
                    arr.push({ id: key, modifiers: {} });
                }
            }
        }

        var normalized = [];
        for (var j = 0; j < arr.length; j++) {
            var item = arr[j];
            if (!item) {
                continue;
            }
            var preferredId = typeof item === 'string' ? item : (item.codeId || item.id);
            if (!preferredId) {
                continue;
            }
            var meta = state.itemIndex.get(preferredId);
            if (!meta && typeof item !== 'string') {
                if (item.id && item.id !== preferredId) {
                    meta = state.itemIndex.get(item.id);
                }
                if (!meta && item.resolvedId) {
                    meta = state.itemIndex.get(item.resolvedId);
                }
                if (!meta && item.alias) {
                    meta = state.itemIndex.get(item.alias);
                }
            }
            if (!meta) {
                meta = state.itemIndex.get(typeof item === 'string' ? item : null);
            }
            if (!meta && typeof item !== 'string' && item.id) {
                meta = state.itemIndex.get(String(item.id));
            }
            var modifiers = extractModifiers(item);
            var alias = meta && meta.alias ? meta.alias : (item.alias || null);
            var resolvedId = meta ? meta.id : (typeof item !== 'string' && item.resolvedId ? item.resolvedId : preferredId);
            var codeId = meta ? (meta.codeId || meta.id) : preferredId;
            normalized.push({
                id: codeId,
                alias: alias,
                resolvedId: resolvedId,
                modifiers: modifiers,
                layer: meta ? meta.layer : (typeof item.layer === 'number' ? item.layer : 0),
                category: meta ? meta.category : (item.category || ''),
                order: meta ? meta.order : (typeof item.order === 'number' ? item.order : normalized.length),
                meta: meta ? meta.meta : item.meta || null
            });
        }
        return enforceCategoryLimits(normalized);
    }

    function encodeOptionsTokens(options) {
        if (!options) {
            return [];
        }
        var tokens = [];
        var keys = Object.keys(options);
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            var value = options[key];
            var formatted = formatModifierValue(value);
            if (formatted === null) {
                continue;
            }
            if (formatted === true) {
                tokens.push(key);
            } else {
                tokens.push(key + '=' + formatted);
            }
        }
        return tokens;
    }

    function encodeCompact(selection, options) {
        ensureAssets();
        var opts = options || {};
        if (opts.options || opts.tokens || (opts.scale && opts.scale !== 1)) {
            return encode(selection, options);
        }
        var normalized = normalizeSelection(selection);
        if (!normalized || !normalized.length) {
            var emptyCode = 'bmoC';
            return opts.wrap === false ? emptyCode : '(' + emptyCode + ')';
        }
        if (!state.compactReverse || !state.compactReverse.length) {
            return encode(selection, options);
        }
        var sorted = sortItems(normalized);
        if (sorted.length > 4095) {
            return encode(selection, options);
        }

        var payloads = [];
        var fallback = false;

        for (var i = 0; i < sorted.length; i++) {
            var item = sorted[i];
            var codeId = item.id || item.codeId || item.resolvedId;
            var compactId = state.compactIndex.get(codeId);
            if (compactId === undefined && item.resolvedId) {
                compactId = state.compactIndex.get(item.resolvedId);
            }
            if (compactId === undefined && item.alias) {
                compactId = state.compactIndex.get(item.alias);
            }
            if (compactId === undefined || compactId === null) {
                fallback = true;
                break;
            }

            var modifiers = item.modifiers && typeof item.modifiers === 'object' ? item.modifiers : {};
            var consumed = Object.create(null);
            var flags = 0;
            var payload = { id: compactId };

            var transformInfo = computeTransformMask(modifiers);
            if (transformInfo && transformInfo.mask !== null && transformInfo.mask !== 0) {
                flags |= COMPACT_FLAG_TF;
                payload.tf = transformInfo.mask & 63;
                consumeModifierKeys(consumed, modifiers, ['tf', 'transform', 'transformmask', 'flipH', 'flipV', 'fliph', 'flipv', 'mirror']);
                if (transformInfo.encodedRotation || sanitizeTransformMask(modifiers.tf) !== null) {
                    consumeModifierKeys(consumed, modifiers, ['rotate', 'rotation']);
                }
            }

            var hueInfo = extractSignedModifier(modifiers, ['h', 'hue']);
            if (hueInfo.encodable && hueInfo.value !== 0) {
                flags |= COMPACT_FLAG_H;
                payload.h = hueInfo.value;
                consumeModifierKeys(consumed, modifiers, ['h', 'hue']);
            }

            var lightInfo = extractSignedModifier(modifiers, ['l', 'lightness']);
            if (lightInfo.encodable && lightInfo.value !== 0) {
                flags |= COMPACT_FLAG_L;
                payload.l = lightInfo.value;
                consumeModifierKeys(consumed, modifiers, ['l', 'lightness']);
            }

            var satInfo = extractSignedModifier(modifiers, ['s', 'saturation']);
            if (satInfo.encodable && satInfo.value !== 0) {
                flags |= COMPACT_FLAG_S;
                payload.s = satInfo.value;
                consumeModifierKeys(consumed, modifiers, ['s', 'saturation']);
            }

            var xInfo = extractSignedModifier(modifiers, ['x']);
            if (xInfo.encodable && xInfo.value !== 0) {
                flags |= COMPACT_FLAG_X;
                payload.x = xInfo.value;
                consumeModifierKeys(consumed, modifiers, ['x']);
            }

            var yInfo = extractSignedModifier(modifiers, ['y']);
            if (yInfo.encodable && yInfo.value !== 0) {
                flags |= COMPACT_FLAG_Y;
                payload.y = yInfo.value;
                consumeModifierKeys(consumed, modifiers, ['y']);
            }

            var extras = {};
            var extraKeys = Object.keys(modifiers);
            for (var k = 0; k < extraKeys.length; k++) {
                var key = extraKeys[k];
                if (consumed[key]) {
                    continue;
                }
                extras[key] = modifiers[key];
            }
            var hasExtraData = Object.keys(extras).length > 0;
            if (hasExtraData) {
                var extraString;
                try {
                    extraString = JSON.stringify(extras);
                } catch (err) {
                    fallback = true;
                    break;
                }
                if (extraString && extraString !== '{}') {
                    flags |= COMPACT_FLAG_EXTRA;
                    payload.extraBytes = utf8Encode(extraString);
                }
            }

            payload.flags = flags;
            payloads.push(payload);
        }

        if (fallback || !payloads.length) {
            return encode(selection, options);
        }

        var writer = createVarWriter();
        for (var index = 0; index < payloads.length; index++) {
            var entry = payloads[index];
            var combined = (entry.id * 128) + (entry.flags & 127);
            writer.writeVarUint(combined);
            if (entry.flags & COMPACT_FLAG_TF) {
                writer.writeVarUint(entry.tf & 63);
            }
            if (entry.flags & COMPACT_FLAG_H) {
                writer.writeVarInt(entry.h);
            }
            if (entry.flags & COMPACT_FLAG_L) {
                writer.writeVarInt(entry.l);
            }
            if (entry.flags & COMPACT_FLAG_S) {
                writer.writeVarInt(entry.s);
            }
            if (entry.flags & COMPACT_FLAG_X) {
                writer.writeVarInt(entry.x);
            }
            if (entry.flags & COMPACT_FLAG_Y) {
                writer.writeVarInt(entry.y);
            }
            if (entry.flags & COMPACT_FLAG_EXTRA) {
                writer.writeVarUint(entry.extraBytes.length);
                writer.writeBytes(entry.extraBytes);
            }
        }

        var bytes = writer.finish();
        var payload = encodeBase64Url(bytes);
        var compactCode = 'bmoC';
        if (payload) {
            compactCode += payload;
        }
        return opts.wrap === false ? compactCode : '(' + compactCode + ')';
    }

    function encode(selection, options) {
        ensureAssets();
        var opts = options || {};
        var items = normalizeSelection(selection);
        if (!items.length && !opts.options && !opts.tokens && !opts.scale) {
            return opts.wrap === false ? CODE_PREFIX : '(' + CODE_PREFIX + ')';
        }

        var sorted = sortItems(items);
        var tokens = encodeTokens(sorted, { allowDuplicates: !!opts.allowDuplicates });

        if (opts.scale && opts.scale !== 1) {
            var scaleToken = 'x' + opts.scale;
            tokens.push(scaleToken);
        }

        if (Array.isArray(opts.tokens)) {
            for (var i = 0; i < opts.tokens.length; i++) {
                var token = opts.tokens[i];
                if (token) {
                    tokens.push(token);
                }
            }
        }

        if (opts.options && typeof opts.options === 'object') {
            var optionTokens = encodeOptionsTokens(opts.options);
            tokens = tokens.concat(optionTokens);
        }

        var code = CODE_PREFIX;
        if (tokens.length) {
            code += '_' + tokens.join('_');
        }

        if (opts.wrap === false) {
            return code;
        }
        return '(' + code + ')';
    }

    function decodeCompact(code) {
        if (!code) {
            return { raw: '', items: [], unknown: [], options: {} };
        }
        ensureAssets();
        var original = String(code).trim();
        var trimmed = original;
        if (!trimmed) {
            return { raw: '', items: [], unknown: [], options: {} };
        }
        if (trimmed.charAt(0) === '(' && trimmed.charAt(trimmed.length - 1) === ')') {
            trimmed = trimmed.slice(1, -1);
        }
        if (trimmed.indexOf('bmoC') !== 0) {
            return null;
        }
        var payload = trimmed.slice(4);
        if (payload.charAt(0) === '_' || payload.charAt(0) === ':' || payload.charAt(0) === '-') {
            payload = payload.slice(1);
        }
        if (!payload) {
            return { raw: trimmed, items: [], unknown: [], options: {} };
        }
        var bytes = decodeBase64Url(payload);
        if (!bytes || !bytes.length) {
            return { raw: trimmed, items: [], unknown: [], options: {} };
        }
        var reader = createVarReader(bytes);
        var resolved = [];
        while (reader.hasMore()) {
            var combined = reader.readVarUint();
            if (combined === null) {
                return null;
            }
            var compactId = combined >>> 7;
            var flags = combined & 127;
            var meta = state.compactReverse[compactId];
            if (!meta) {
                return null;
            }
            var modifiers = {};
            if (flags & COMPACT_FLAG_TF) {
                var maskValue = reader.readVarUint();
                if (maskValue === null) {
                    return null;
                }
                var normalizedMask = sanitizeTransformMask(maskValue);
                if (normalizedMask === null) {
                    return null;
                }
                modifiers.tf = normalizedMask;
            }
            if (flags & COMPACT_FLAG_H) {
                var hueValue = reader.readVarInt();
                if (hueValue === null) {
                    return null;
                }
                if (hueValue !== 0) {
                    modifiers.h = hueValue;
                }
            }
            if (flags & COMPACT_FLAG_L) {
                var lightValue = reader.readVarInt();
                if (lightValue === null) {
                    return null;
                }
                if (lightValue !== 0) {
                    modifiers.l = lightValue;
                }
            }
            if (flags & COMPACT_FLAG_S) {
                var saturationValue = reader.readVarInt();
                if (saturationValue === null) {
                    return null;
                }
                if (saturationValue !== 0) {
                    modifiers.s = saturationValue;
                }
            }
            if (flags & COMPACT_FLAG_X) {
                var xValue = reader.readVarInt();
                if (xValue === null) {
                    return null;
                }
                modifiers.x = xValue;
            }
            if (flags & COMPACT_FLAG_Y) {
                var yValue = reader.readVarInt();
                if (yValue === null) {
                    return null;
                }
                modifiers.y = yValue;
            }
            if (flags & COMPACT_FLAG_EXTRA) {
                var extraLength = reader.readVarUint();
                if (extraLength === null) {
                    return null;
                }
                var extraBytes = reader.readBytes(extraLength);
                if (extraBytes === null) {
                    return null;
                }
                var extraString = utf8Decode(extraBytes);
                if (extraString && extraString.length) {
                    try {
                        var extraObj = JSON.parse(extraString);
                        if (extraObj && typeof extraObj === 'object') {
                            var extraKeys = Object.keys(extraObj);
                            for (var e = 0; e < extraKeys.length; e++) {
                                var extraKey = extraKeys[e];
                                var extraValue = extraObj[extraKey];
                                if (extraValue !== undefined) {
                                    modifiers[extraKey] = extraValue;
                                }
                            }
                        }
                    } catch (err) {
                        return null;
                    }
                }
            }
            resolved.push({
                id: meta.id,
                src: meta.src,
                layer: meta.layer,
                order: meta.order,
                category: meta.category,
                modifiers: modifiers,
                meta: meta.meta
            });
        }

        resolved = enforceCategoryLimits(resolved);

        return {
            raw: trimmed,
            items: resolved,
            unknown: [],
            options: {}
        };
    }

    function loadImage(src) {
        if (!src) {
            return Promise.reject(new Error('Missing image source.'));
        }
        if (state.loadPromises.has(src)) {
            return state.loadPromises.get(src);
        }
        var promise = new Promise(function (resolve, reject) {
            var image = new Image();
            image.crossOrigin = 'anonymous';
            image.onload = function () {
                resolve(image);
            };
            image.onerror = function (err) {
                reject(err);
            };
            image.src = src;
        });
        state.loadPromises.set(src, promise);
        return promise;
    }

    function rgbToHsl(r, g, b) {
        r /= 255;
        g /= 255;
        b /= 255;
        var max = Math.max(r, g, b);
        var min = Math.min(r, g, b);
        var h = 0;
        var s = 0;
        var l = (max + min) / 2;

        if (max !== min) {
            var d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            switch (max) {
                case r:
                    h = (g - b) / d + (g < b ? 6 : 0);
                    break;
                case g:
                    h = (b - r) / d + 2;
                    break;
                case b:
                    h = (r - g) / d + 4;
                    break;
            }
            h /= 6;
        }

        return [h * 360, s * 100, l * 100];
    }

    function hue2rgb(p, q, t) {
        if (t < 0) {
            t += 1;
        }
        if (t > 1) {
            t -= 1;
        }
        if (t < 1 / 6) {
            return p + (q - p) * 6 * t;
        }
        if (t < 1 / 2) {
            return q;
        }
        if (t < 2 / 3) {
            return p + (q - p) * (2 / 3 - t) * 6;
        }
        return p;
    }

    function hslToRgb(h, s, l) {
        h /= 360;
        s /= 100;
        l /= 100;

        var r;
        var g;
        var b;

        if (s === 0) {
            r = g = b = l; // achromatic
        } else {
            var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            var p = 2 * l - q;
            r = hue2rgb(p, q, h + 1 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3);
        }

        return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
    }

    function applyColorAdjust(ctx, width, height, modifiers) {
        var hue = toNumber(modifiers.h);
        var light = toNumber(modifiers.l);
        var sat = toNumber(modifiers.s);

        if (hue === null && light === null && sat === null) {
            return;
        }

        var imageData = ctx.getImageData(0, 0, width, height);
        var data = imageData.data;

        for (var i = 0; i < data.length; i += 4) {
            var alpha = data[i + 3];
            if (alpha === 0) {
                continue;
            }
            var hsl = rgbToHsl(data[i], data[i + 1], data[i + 2]);
            if (hue !== null) {
                hsl[0] = (hsl[0] + hue + 360) % 360;
            }
            if (sat !== null) {
                hsl[1] = clamp(hsl[1] + sat, 0, 100);
            }
            if (light !== null) {
                hsl[2] = clamp(hsl[2] + light, 0, 100);
            }
            var rgb = hslToRgb(hsl[0], hsl[1], hsl[2]);
            data[i] = rgb[0];
            data[i + 1] = rgb[1];
            data[i + 2] = rgb[2];
        }

        ctx.putImageData(imageData, 0, 0);
    }

    function drawLayer(ctx, image, modifiers) {
        var width = ctx.canvas.width;
        var height = ctx.canvas.height;
        var tempCanvas = document.createElement('canvas');
        tempCanvas.width = width;
        tempCanvas.height = height;
        var tempCtx = tempCanvas.getContext('2d');

        tempCtx.imageSmoothingEnabled = false;
        tempCtx.mozImageSmoothingEnabled = false;
        tempCtx.webkitImageSmoothingEnabled = false;
        tempCtx.msImageSmoothingEnabled = false;

        tempCtx.save();

        var translateX = toNumber(modifiers.x) || 0;
        var translateY = toNumber(modifiers.y) || 0;
        var rotate = toNumber(modifiers.rotate);
        if (rotate === null) {
            rotate = toNumber(modifiers.rotation) || 0;
        }
        var scale = toNumber(modifiers.scale);
        var scaleX = toNumber(modifiers.scaleX);
        var scaleY = toNumber(modifiers.scaleY);
        var transformState = resolveTransformState(modifiers || {});
        var flipH = transformState.flipH;
        var flipV = transformState.flipV;
        var resolvedRotation = transformState.rotation;

        var sx = (scaleX !== null ? scaleX : (scale !== null ? scale : 1)) * (flipH ? -1 : 1);
        var sy = (scaleY !== null ? scaleY : (scale !== null ? scale : 1)) * (flipV ? -1 : 1);

        tempCtx.save();
        tempCtx.translate(width / 2 + translateX, height / 2 + translateY);
        var rotateValue = resolvedRotation || rotate;
        if (rotateValue) {
            tempCtx.rotate((rotateValue * Math.PI) / 180);
        }
        tempCtx.scale(sx, sy);
        tempCtx.drawImage(image, -width / 2, -height / 2, width, height);
        tempCtx.restore();

        applyColorAdjust(tempCtx, width, height, modifiers || {});

        ctx.drawImage(tempCanvas, 0, 0);
    }

    function prepareCanvas(element, options) {
        var width = options && options.width ? options.width : state.options.width;
        var height = options && options.height ? options.height : state.options.height;
        var doc = (element && element.ownerDocument) || (global && global.document) || null;

        var canvas = element && element.__bmojiCanvas ? element.__bmojiCanvas : null;
        if ((!canvas || typeof canvas.getContext !== 'function') && element && element.querySelector) {
            var existing = element.querySelector('canvas');
            if (existing && typeof existing.getContext === 'function') {
                canvas = existing;
            }
        }
        if ((!canvas || typeof canvas.getContext !== 'function') && doc) {
            canvas = doc.createElement('canvas');
        }
        if (!canvas || typeof canvas.getContext !== 'function') {
            return null;
        }

        if (element) {
            element.__bmojiCanvas = canvas;
        }

        if (canvas.classList && !canvas.classList.contains('bmoji-canvas')) {
            canvas.classList.add('bmoji-canvas');
        } else if (!canvas.classList) {
            var className = canvas.className || '';
            if ((' ' + className + ' ').indexOf(' bmoji-canvas ') === -1) {
                canvas.className = (className ? className + ' ' : '') + 'bmoji-canvas';
            }
        }

        canvas.style.width = 21 + 'px';
        canvas.style.height = 21 + 'px';

        if (canvas.width !== width || canvas.height !== height) {
            canvas.width = width;
            canvas.height = height;
        }

        var ctx = canvas.getContext('2d');
        if (!ctx) {
            return null;
        }
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.imageSmoothingEnabled = false;
        ctx.mozImageSmoothingEnabled = false;
        ctx.webkitImageSmoothingEnabled = false;
        ctx.__bmojiWidth = width;
        ctx.__bmojiHeight = height;

        return ctx;
    }

    function sortItems(items) {
        return items.slice().sort(function (a, b) {
            if (a.layer !== b.layer) {
                return a.layer - b.layer;
            }
            if (a.category !== b.category) {
                return a.category.localeCompare(b.category);
            }
            if (a.order !== b.order) {
                return a.order - b.order;
            }
            return 0;
        });
    }

    function createRenderCacheEntry(ctx, width, height) {
        var imageData = null;
        var dataUrl = null;
        try {
            imageData = ctx.getImageData(0, 0, width, height);
        } catch (err) {
            imageData = null;
        }
        try {
            dataUrl = ctx.canvas.toDataURL('image/png');
        } catch (err) {
            dataUrl = null;
        }
        if (!imageData && !dataUrl) {
            return null;
        }
        return {
            width: width,
            height: height,
            imageData: imageData,
            dataUrl: dataUrl,
            imageElement: null,
            bitmap: null,
            loadingPromise: null
        };
    }
    
    function renderElement(element, rawOptions) {
        ensureAssets();

        if (!element) {
            return Promise.resolve(null);
        }

        var code = element.getAttribute('data-code') || element.textContent;
        if (!code) {
            return Promise.resolve(null);
        }

        var parsed = decode(code);
        if (!parsed.items.length) {
            element.dataset.bmojiStatus = 'empty';
            return Promise.resolve(null);
        }

        var defaultWidth = state.options.width;
        var defaultHeight = state.options.height;
        var requestedWidth = defaultWidth;
        var requestedHeight = defaultHeight;
        if (rawOptions && typeof rawOptions.width === 'number' && isFinite(rawOptions.width)) {
            requestedWidth = rawOptions.width;
        }
        if (rawOptions && typeof rawOptions.height === 'number' && isFinite(rawOptions.height)) {
            requestedHeight = rawOptions.height;
        }

        var renderAsImage = !(rawOptions && rawOptions.renderAsImage === false);
        var allowCache = !(rawOptions && rawOptions.cache === false);
        var doc = element.ownerDocument || (global && global.document) || null;
        var normalizedCode = parsed.raw || String(code).trim();
        var cacheKey = allowCache ? makeRenderCacheKey(normalizedCode, requestedWidth, requestedHeight) : null;
        
        function showImageFromDataUrl(dataUrl, width, height) {
            if (!doc || !dataUrl) {
                return false;
            }
            var imageElement = element.__bmojiImage || (element.querySelector ? element.querySelector('img[data-bmoji-role="image"]') : null);
            if (!imageElement || imageElement.ownerDocument !== doc) {
                imageElement = doc.createElement('img');
                imageElement.setAttribute('data-bmoji-role', 'image');
                imageElement.setAttribute('alt', code);
                imageElement.className = 'bmoji-image';
                element.__bmojiImage = imageElement;
            }
            element.__bmojiImage = imageElement;
            imageElement.src = dataUrl;
            if (typeof width === 'number' && width > 0) {
                imageElement.width = width;
            }
            if (typeof height === 'number' && height > 0) {
                imageElement.height = height;
            }
            imageElement.style.width = 21 + 'px';
            imageElement.style.height = 21 + 'px';
            if (element.firstChild !== imageElement || element.childNodes.length !== 1) {
                while (element.firstChild) {
                    element.removeChild(element.firstChild);
                }
                element.appendChild(imageElement);
            }
            imageElement.style.display = '';
            var existingCanvas = element.__bmojiCanvas;
            if (existingCanvas && existingCanvas.parentNode === element) {
                existingCanvas.style.display = 'none';
                element.removeChild(existingCanvas);
            }
            return true;
        }
        if (renderAsImage && allowCache && cacheKey) {
            var cachedEntry = getCachedRender(cacheKey);
            if (cachedEntry && cachedEntry.dataUrl) {
                // console.log(cacheKey, cachedEntry);
                if (showImageFromDataUrl(cachedEntry.dataUrl, requestedWidth, requestedHeight)) {
                    element.dataset.bmojiStatus = 'rendered';
                    return Promise.resolve(element.__bmojiCanvas || null);
                }
            }
        }

        var ctx = prepareCanvas(element, rawOptions);
        if (!ctx) {
            element.dataset.bmojiStatus = 'error';
            return Promise.resolve(null);
        }

        var canvas = ctx.canvas;
        var logicalWidth = ctx.__bmojiWidth || canvas.width;
        var logicalHeight = ctx.__bmojiHeight || canvas.height;
        ctx.clearRect(0, 0, logicalWidth, logicalHeight);

        if (allowCache) {
            cacheKey = makeRenderCacheKey(normalizedCode, logicalWidth, logicalHeight);
        }

        function showCanvas() {
            if (canvas.parentNode !== element) {
                while (element.firstChild) {
                    element.removeChild(element.firstChild);
                }
                element.appendChild(canvas);
            }
            canvas.style.display = '';
            var imageNode = element.__bmojiImage || (element.querySelector ? element.querySelector('img[data-bmoji-role="image"]') : null);
            if (imageNode && imageNode.parentNode === element) {
                imageNode.style.display = 'none';
                element.removeChild(imageNode);
            }
        }

        var sorted = sortItems(parsed.items);
        var promises = sorted.map(function (item) {
            return loadImage(item.src).then(function (image) {
                return { image: image, modifiers: item.modifiers || {} };
            });
        });

        return Promise.all(promises).then(function (layers) {
            for (var i = 0; i < layers.length; i++) {
                var layer = layers[i];
                drawLayer(ctx, layer.image, layer.modifiers);
            }

            if (renderAsImage) {
                var dataUrl = null;
                try {
                    dataUrl = canvas.toDataURL('image/png');
                } catch (imageErr) {
                    if (global && global.console && console.warn) {
                        console.warn('[bmoji] Unable to export canvas as image', imageErr);
                    }
                }
                if (dataUrl && showImageFromDataUrl(dataUrl, logicalWidth, logicalHeight)) {
                    if (allowCache && cacheKey) {
                        setRenderCache(cacheKey, {
                            width: logicalWidth,
                            height: logicalHeight,
                            dataUrl: dataUrl,
                            imageElement: null,
                            bitmap: null,
                            loadingPromise: null
                        });
                    }
                } else {
                    if (allowCache && cacheKey && state.renderCache) {
                        state.renderCache.delete(cacheKey);
                    }
                    showCanvas();
                }
            } else {
                showCanvas();
            }

            element.dataset.bmojiStatus = 'rendered';
            return canvas;
        }).catch(function (err) {
            element.dataset.bmojiStatus = 'error';
            if (allowCache && cacheKey && state.renderCache) {
                state.renderCache.delete(cacheKey);
            }
            if (global && global.console && console.error) {
                console.error('[bmoji] Failed to render', code, err);
            }
            return null;
        });
    }

    function renderCode(code, options) {
        if (!code) {
            return Promise.resolve(null);
        }
        var wrapper = global && global.document ? global.document.createElement('span') : null;
        if (!wrapper) {
            return Promise.resolve(null);
        }
        wrapper.setAttribute('data-code', code);
        return renderElement(wrapper, options).then(function () {
            return wrapper.querySelector('canvas');
        });
    }

    async function renderAll(root, options) {
        ensureAssets();
        var container = root || (global && global.document ? global.document : null);
        if (!container || !container.querySelectorAll) {
            return Promise.resolve([]);
        }
        var selector = (options && options.selector) || state.options.selector;
        var nodes = container.querySelectorAll(selector);
        var renders = [];
        for (var i = 0; i < nodes.length; i++) {
            await renderElement(nodes[i], options);
            // renders.push(renderElement(nodes[i], options));
        }
        return Promise.all(renders);
    }

    function configure(options) {
        if (!options) {
            return;
        }
        var widthChanged = false;
        var heightChanged = false;
        if (typeof options.width === 'number' && options.width !== state.options.width) {
            state.options.width = options.width;
            widthChanged = true;
        }
        if (typeof options.height === 'number' && options.height !== state.options.height) {
            state.options.height = options.height;
            heightChanged = true;
        }
        if (options.selector) {
            state.options.selector = options.selector;
        }
        if (widthChanged || heightChanged) {
            clearRenderCache();
        }
    }

    function observe(root, options) {
        if (!global || !global.MutationObserver) {
            return;
        }
        var container = root || global.document.body;
        if (state.observer) {
            state.observer.disconnect();
        }
        var ElementCtor = global && global.Element ? global.Element : null;
        state.observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                if (!mutation.addedNodes || !mutation.addedNodes.length) {
                    return;
                }
                for (var i = 0; i < mutation.addedNodes.length; i++) {
                    var node = mutation.addedNodes[i];
                    if (ElementCtor ? !(node instanceof ElementCtor) : node.nodeType !== 1) {
                        continue;
                    }
                    if (node.matches && node.matches(state.options.selector)) {
                        renderElement(node, options);
                    }
                    var descendants = node.querySelectorAll ? node.querySelectorAll(state.options.selector) : [];
                    for (var j = 0; j < descendants.length; j++) {
                        renderElement(descendants[j], options);
                    }
                }
            });
        });
        state.observer.observe(container, { childList: true, subtree: true });
    }

    function disconnectObserver() {
        if (state.observer) {
            state.observer.disconnect();
            state.observer = null;
        }
    }

    var api = global.Bmoji || {};

    api.decode = decode;
    api.encode = encode;
    api.decodeCompact = decodeCompact;
    api.encodeCompact = encodeCompact;
    api.render = renderElement;
    api.renderAll = renderAll;
    api.renderCode = renderCode;
    api.configure = configure;
    api.observe = observe;
    api.disconnect = disconnectObserver;
    api.clearRenderCache = clearRenderCache;
    api.setAssets = setAssets;
    api.getAssets = function () {
        ensureAssets();
        return state.assets;
    };
    api.savedBmo = {
        list: listSavedBmoEntries,
        create: createSavedBmoEntry,
        update: updateSavedBmoEntry,
        remove: deleteSavedBmoEntry,
        delete: deleteSavedBmoEntry,
        clear: clearSavedBmoEntries,
        createRenderer: createSavedBmoListRenderer,
        renderList: function (container, options) {
            var renderer = createSavedBmoListRenderer(container, options);
            if (!renderer) {
                return null;
            }
            renderer.refresh();
            return renderer;
        },
        formatTitle: getSavedBmoDisplayName,
        formatTimestamp: formatSavedBmoTimestamp,
        keys: {
            storage: SAVED_BMO_STORAGE_KEY,
            updatedAt: SAVED_BMO_UPDATED_KEY
        }
    };
    api.version = '0.1.0';

    global.Bmoji = api;

    if (global && global.document) {
        if (global.document.readyState === 'loading') {
            global.document.addEventListener('DOMContentLoaded', function () {
                renderAll();
            });
        } else {
            renderAll();
        }
    }
})(typeof window !== 'undefined' ? window : this);
