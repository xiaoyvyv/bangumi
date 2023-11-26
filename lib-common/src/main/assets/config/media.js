var tmp = [];
document.querySelectorAll("ul.grouped:nth-child(2) li a").forEach(item => {
    tmp.push({
        title: item.innerText,
        value: item.href.replace("https://bangumi.tv/anime/browser", "")
    })
});
console.log(JSON.stringify(tmp));