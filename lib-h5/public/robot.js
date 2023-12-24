// ==UserScript==
// @name         伪春菜人格切换
// @version      0.1
// @description  给伪春菜实装Chobits Terminal中的人格。
// @author       1ra
// @include      /^https?://(bgm\.tv|bangumi\.tv|chii\.in).*$/
// ==/UserScript==

(function () {
    if (window.location.pathname.match(/^\/settings\/ukagaka$/) !== null) {
        let psn = localStorage.getItem("upc_psn");
        $.post("/terminal", "input=personality&cmd=personality&arg=personality", function (data) {
            $("#columnA").append(`<table align="center" width="98%" cellspacing="0" cellpadding="5" class="settings"><tbody>
            <tr><td valign="top" width="12%">人格</td>
            <td valign="top"><select name="personalitysetnew" class="form" style="width:390px"><option value="0" selected>-默认人格-</option></select></td></tr></tbody></table>`);
            let result = [...data.matchAll(/(\d+) (.+?) \| Speech count:(\d+).+? <br \/>/g)];
            let select = $("[name=personalitysetnew]");
            result.forEach(function (item) {
                if (parseInt(item[3]) > 0) select.append(new Option(item[2] + ' [' + item[3] + '] ', item[1]));
            });
            select.val(psn ? psn : 0);
            select.change(function () {
                psn = select.val();
                localStorage.setItem("upc_psn", psn);
                if (psn == 0) localStorage.removeItem("upc_data");
                else {
                    $.post("/terminal", "input=list&cmd=list&arg=list&cur_psn=" + psn, function (data) {
                        let speech = [...data.matchAll(/\d+ (.+?) by .+? <br \/>/g)];
                        localStorage.setItem("upc_data", JSON.stringify(speech));
                        location.reload();
                    });
                }
            });
        });
    } else if (window.location.pathname.match(/^\/settings/) !== null) return;
    else if (localStorage.getItem("upc_data")) {
        let speechData = JSON.parse(localStorage.getItem("upc_data"));
        $("#robot_speech").html(speechData[Math.floor(Math.random() * speechData.length)][1]);
    }
})();