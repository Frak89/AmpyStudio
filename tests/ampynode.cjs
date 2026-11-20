const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path');
const engine=fs.readFileSync(path.join(__dirname,'../src/engine.js'),'utf8');
const graph=new Function(engine+'\nreturn AmpyNode;')();
const g=graph.example('button','alarm'),state={tags:new Map(g.tags.map(t=>[t.name,t.value]))},actions=[];
assert.equal(graph.run(g,{kind:'warning'},state,{action:n=>actions.push(n.kind)}).steps,5);
assert.equal(state.tags.get('DB_Alarm.Warning'),1);assert.deepEqual(actions,['animate','log']);
for(const [value,expected] of [[95,.5],[40,0],[80,0]]){state.tags.set('DB_Core.Flow',value);const a=[];graph.run(g,{kind:'tag',tag:'DB_Core.Flow'},state,{action:n=>a.push(n.value)});assert.deepEqual(a,[expected]);}
const cycle=graph.normalize({nodes:[{id:'e',kind:'event',target:'stale'},{id:'a',kind:'log'},{id:'b',kind:'log'}],edges:[{from:'e',to:'a'},{from:'a',to:'b'},{from:'b',to:'a'}]});
assert.equal(graph.run(cycle,{kind:'manual'},state).halted,true);
assert.deepEqual(graph.normalize(JSON.parse(JSON.stringify(g))),g);
console.log('AmpyNode regression tests passed');
