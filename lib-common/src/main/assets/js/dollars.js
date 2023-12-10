(function () {
    const metaTag = document.createElement('meta');
    metaTag.name = 'viewport';
    metaTag.content = 'width=device-width, initial-scale=1.0, shrink-to-fit=no, maximum-scale=1, user-scalable=0';
    document.head.appendChild(metaTag);

    // CSS 优化
    const styleElement = document.createElement('style');
    styleElement.innerHTML = `
        #header {
            width: 100% !important;
            position: fixed !important;
            bottom: 0 !important;
            top: unset !important;
        }

        form {
            box-sizing: border-box !important;
        }

        textarea {
            width: 90% !important;
            box-sizing: border-box !important;
        }

        #chatBox {
            width: 100% !important;
            background: #262626;
        }

        textarea[name=message] {
            background-color: #333333 !important;
            color: white !important;
            outline: none;
            height: 80px !important;
            resize: none !important;
        }

        #toolBox {
            color: #666 !important;
            text-shadow: 0px -1px #333 !important;
        }

        input.tweet {
            background: -webkit-gradient(linear, left top, left bottom, from(#666), to(#777));
            background: -moz-linear-gradient(top, #666, #777);
            color: #eee !important;
        }

        #chatList {
            width: 100% !important;
            margin: 16px 0 165px 0 !important;
            box-sizing: border-box !important;
        }
        
        #chatList ul {
            margin: 16px !important;
        }
        
        #chatList div.bubble div.content {
            max-width: 100% !important;
        }`;
    document.head.appendChild(styleElement);
    document.querySelector('textarea[name=message]').placeholder = '最新消息在最上方，点击头像可以@对方，长按头像打开用户主页';
})();