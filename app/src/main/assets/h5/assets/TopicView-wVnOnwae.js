import{d as b,r as a,a as x,o as N,c as r,b as i,t as u,e as s,u as g,i as T,g as p,w as h,n as k,h as v}from"./index-fmpy9jEk.js";import{c,R as I,E as R,_ as D,C as E,B as J,a as M,W as H}from"./BottomSpinnerView-2frrTuH7.js";const L={key:0,class:"topic",id:"topic"},A={class:"topic-title"},O={class:"topic-info"},z={class:"topic-author"},F={class:"topic-time"},W=["innerHTML"],X={key:0,class:"emoji"},Y=i("div",{style:{flex:"1"}},null,-1),q={key:1,class:"divider"},j=10,U=b({__name:"TopicView",setup(G){const t=a({}),_=a(),w=a(new Date().getDate()),m=a(1),d=a("desc"),l=x([]),f=a("哼！Bangumi老娘我是有底线的人"),y={loadTopicDetail:async e=>{t.value=e,await k(),c.optContentJs(_.value)}},V=async e=>{const o=window.android.onLoadComments(m.value,j,d.value),n=JSON.parse(o);n.length==0?e.complete():(await c.delay(200),l.push(...n),m.value++,await k(),n.length<j?e.complete():e.loaded())},C=e=>{var o;return{emojiParam:e.emojiParam,id:(o=e.emojiParam)==null?void 0:o.likeCommentId,gh:e.gh}},S=e=>{let o=C(t.value);window.android&&window.android.onClickCommentAction(JSON.stringify(o),e.clientX,e.clientY)},B=(e,o)=>{e==t.value.emojiParam.likeCommentId&&(t.value.emojis=o)};return N(()=>{window.robotSay=e=>{f.value=e},c.initComment(B,()=>l,e=>{d.value=e,m.value=1,l.length=0,w.value++}),window.topic=y,window.mounted=!0}),(e,o)=>{var n;return t.value.id?(v(),r("div",L,[i("div",A,u(t.value.title),1),i("div",O,[i("div",z,u(t.value.userName),1),i("div",F,u(t.value.time),1)]),s(I,{related:t.value.related},null,8,["related"]),i("div",{class:"topic-content",ref_key:"topicContentRef",ref:_,innerHTML:g(c).optText(t.value.content)},null,8,W),(n=t.value.emojiParam)!=null&&n.enable?(v(),r("div",X,[s(R,{class:"topic-emoji",emojis:t.value.emojis,comment:C(t.value)},null,8,["emojis","comment"]),Y,i("img",{class:"action",smileid:"",src:D,alt:"action",onClick:o[0]||(o[0]=T(P=>S(P),["stop"]))})])):p("",!0),t.value.content?(v(),r("div",q)):p("",!0),s(E,{target:"#topic",comments:l,sort:d.value},null,8,["comments","sort"]),s(g(H),{class:"loading",target:"#topic",identifier:w.value,distance:300,onInfinite:V},{spinner:h(()=>[s(J)]),complete:h(()=>[s(M,{message:f.value},null,8,["message"])]),_:1},8,["identifier"])])):p("",!0)}}});export{U as default};
