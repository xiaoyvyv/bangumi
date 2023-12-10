(function () {
    const metaTag = document.createElement('meta');
    metaTag.name = 'viewport';
    metaTag.content = 'width=device-width, initial-scale=1.0, shrink-to-fit=no, maximum-scale=1, user-scalable=0';
    document.head.appendChild(metaTag);

    // CSS 优化
    const styleElement = document.createElement('style');
    styleElement.innerHTML = `
        #chatBox {
            width: 100% !important;
        }
        
        #chatList {
            width: 100% !important;
        }
        
        #chatList ul {
            margin: 16px !important;
        }
        
        #chatList div.bubble div.content {
            max-width: 100% !important;
        }`;
    document.head.appendChild(styleElement);
})();