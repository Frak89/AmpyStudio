(() => {
 if(document.getElementById('mobileStyle')) return;
 const style=document.createElement('style');style.id='mobileStyle';
 style.textContent=`
 body{font-size:13px;padding-bottom:70px}
 header{padding:10px;gap:5px;align-items:center} header b{font-size:19px}
 header small{font-size:9px} header .toolbar{margin:0}
 button,.button,select{min-height:44px;padding:10px 9px;touch-action:manipulation}
 #mobileNav{position:fixed;bottom:0;left:0;right:0;display:flex;gap:5px;background:#091316;z-index:50;border-top:1px solid #3b5e51;padding:6px}
 #mobileNav button{flex:1;min-width:0;padding:7px 2px;font-size:11px}
 main{display:flex!important;flex-direction:column;padding:6px;gap:6px}
 main>aside:last-child{display:block}
 .work{order:-1;padding:6px} #stage{max-height:48vh;min-height:180px}
 #glyphs{grid-template-columns:repeat(6,1fr)} #glyphs button{min-height:48px}
 #glyphs svg{height:32px} #tracks>div{height:38px}
 .lane{height:32px}.key{width:18px;height:18px;top:7px;min-height:0;padding:0}
 input[type=range]{min-height:32px} input[type=text],input[type=number]{min-height:38px}
 body[data-panel=canvas] main>aside, body[data-panel=canvas] #mobileTimeline{display:none!important}
 body[data-panel=objects] main>aside:last-child, body[data-panel=objects] #mobileTimeline{display:none!important}
 body[data-panel=inspector] main>aside:first-child, body[data-panel=inspector] #mobileTimeline{display:none!important}
 body[data-panel=timeline] main>aside{display:none!important}
 @media(min-width:800px) and (orientation:landscape) {#stage{max-height:50vh} #glyphs{grid-template-columns:repeat(8,1fr)}}
 `;document.head.appendChild(style);
 const work=document.querySelector('.work'), heading=[...work.querySelectorAll('h3')].find(e=>e.textContent.includes('TIMELINE'));
 if(heading){const timeline=document.createElement('div');timeline.id='mobileTimeline';work.insertBefore(timeline,heading);while(timeline.nextSibling)timeline.appendChild(timeline.nextSibling);}
 const nav=document.createElement('nav');nav.id='mobileNav';nav.setAttribute('aria-label','Editor panels');
 const names={canvas:'CANVAS',objects:'OBJECTS',inspector:'INSPECTOR',timeline:'TIMELINE'};
 Object.entries(names).forEach(([key,name])=>{const b=document.createElement('button');b.textContent=name;b.onclick=()=>{document.body.dataset.panel=key;[...nav.children].forEach(el=>el.classList.toggle('active',el===b));if(key==='canvas')window.scrollTo(0,0);else requestAnimationFrame(()=>{const target=key==='timeline'?document.getElementById('mobileTimeline'):document.querySelector(key==='objects'?'main>aside:first-child':'main>aside:last-child');target?.scrollIntoView({block:'start'});});};nav.appendChild(b);});
 document.body.appendChild(nav);document.body.dataset.panel='canvas';nav.firstChild.classList.add('active');
 if(window.AmpyAndroid){window.download=(text,name)=>AmpyAndroid.save(text,name);}
})();