// JavaScript Document
(function(){
	var _S = function(obj){
		var that = this;
		that.e = null;
		
		that._constructor = 'Sbory';
		
		if(obj._constructor == 'Sbory'){			
				return obj;				
		}
		
		if(typeof(obj) == 'string'){	
			that.e =  document.getElementById(obj) || null;
			if(that.e == null && typeof window.console != 'undefined' )console.info("[Warm] element "+obj+" is not exist!!!");
		}else{			
			that.e = obj;			
		}
	}
	
	 window.$ = window.Sbory = function(obj){return new _S(obj)}
	
		
	 _S.prototype = {
		
		style : function(k,v){
			var that = this,
				e = that.e;
			if(typeof(v) == 'undefined'){
				return e.style[k];
			}else{
				e.style[k] = v;
				return that;
			}
		},
		
		
		bgImage : function(v){
			return this.style('backgroundImage',typeof(v)!='undefined'?'url('+v+')':v);
		}
	 }
	 
	 Sbory.extend =  function(){
		var a = arguments,
			l = a.length;
		if(l == 1){
			for(var pro in a[0]){					 
					_S.prototype[pro] = a[0][pro];
			}
		}
		if(l == 2){
			_S.prototype[a[0]] = a[1];
		}
		
	};
	

	Sbory.plugs = {};
	
	
	
})();




/************    常用属性     *****************/
$.browser = (function(){
	var ver = 	navigator.userAgent,
		i = 'indexOf';				
	
	if(ver[i]('iPanel') != -1){
		return 'iPanel';
	}else if(ver[i]('MSIE') != -1){
		return 'IE';
	}else if(ver[i]('Coship') != -1){
		return 'Coship';
	}else if(ver[i]('Chrome') != -1){
		return 'Chrome';
	}else if(ver[i]('QtEmbedded') != -1){/**海南 高清视信(CHNAVS) **/
		return 'Chnavs';
	}else if(ver[i]('Qt') != -1){
		return 'Hisu';
	}else if(ver[i]('Sunniwell') != -1){
		return 'Sunniwell';
	}else if(ver[i]('zgm9004_vortex') != -1){
		return 'OTTSW';
	}else if(ver[i]('RocME') != -1){
		return 'RocME';
	}else if(ver[i]('Webdroid') != -1){
		return 'Webdroid';
	}else if(ver[i]('Android') != -1){
		return 'Android';
	}else if(ver[i]('VVB') != -1){
		return 'WeiQiao';
	}else if(ver[i]('7B500') != -1){
		return 'GXCATV';
	}
	return 'iPanelAdvanced';
})();

if($.browser == 'WeiQiao'){
	window.addEventListener('load',addButtonEvent,false);
	function addButtonEvent(){
		document.addEventListener("backbutton", function(){//返回键要通过添加监听才能够捕获
			var tmpObj = {key:'BACK',code:340,type:1};
			$.plugs.keyProcess(tmpObj.key,tmpObj);
		});

		document.addEventListener("menubutton", function(){//菜单键要通过添加监听才能够捕获
			var tmpObj = {key:'MENU',code:513,type:1};
			var ret = $.plugs.keyProcess(tmpObj.key,tmpObj) || 0;
			if(ret)ret = $.plugs.keyDefaultProcess(tmpObj) || 0;
			return ret;
		});
	}
}
/**************   常用函数区域   *************/
$.util = $.util || {};
$.util.typeOf = function(obj){
	var type = 'o';
	switch(typeof obj){
		case 'string':
			type = 's';
			break;
		case 'number':
			type = 'i';
			break;
		case 'object':
			if( obj instanceof Array){
				type='a';
			}
			break;	
		case 'boolean':
			type = 'b';
			break;
	}	
	return type;
}
//只限简单对象，数组，字符串，数字
$.util.serialize = function(obj){
	var type = this.typeOf(obj);
	var ret ='';
	switch(type){
		case 'a':
			var al = obj.length;
			ret = al + ':[';
			for(var i=0; i<al; i++){
				ret += this.serialize(obj[i]) +';';
			}
			ret += ']';
			break;
		case 'o':
			var ol = 0;

			for(var k in obj){
				ol++;
				ret += this.serialize(k) +';' + this.serialize(obj[k])+";";
			}
			ret = ol + ':{' + ret + '}';
			break;
		case 's':
			ret =obj.length+':'+  '"'+ obj + '"';
			break;
		case 'i':
			ret = obj;
			break;
		case 'b':
			ret = obj?1:0;
			break;
	}
	return type + ':' + ret;
}
$.util.unserialize = function(str){
	function _unserialize(str){
		var type = str.substring(0,1);
		var value = {value:'',str:''};
		if(type=='i' || type=='b'){
			var spos = str.indexOf(':');
			var epos = str.indexOf(';',spos+1);
			if( epos < 0 ){
				epos = str.length;
			}
			var numberStr = str.substring(spos+1,epos);
			value = parseInt(numberStr);
			var nf = parseFloat(numberStr);
			if(nf>value){
				value = nf;
			}
			return {str:str.substring(epos+1),value:type=='b'?(value==1):value};
		}else {
			
			var spos = str.indexOf(':');
			var epos = str.indexOf(':',spos+1);
			var len = parseInt(str.substring(spos+1,epos));
			if(type=='s'){
				spos = str.indexOf(':"')+2;
				epos = spos+len;
				value = str.substring(spos,epos);
				return {str:str.substring(epos+2),value:value};
			}else if(type=='a'){
				spos = str.indexOf('[')+1;
				str = str.substring(spos);
				value = [];
				for(var i=0;i<len;i++){
					var tmpobj = _unserialize(str);
					str = tmpobj.str;
					value.push(tmpobj.value);
				}
				epos = str.indexOf(']');
				return {str:str.substring(epos+2),value:value};
			}else if(type=='o'){
				spos = str.indexOf('{')+1;
				str = str.substring(spos);
				value = {};
				for(var i=0;i<len;i++){
					var kObj = _unserialize(str);
					str = kObj.str;

					var vObj = _unserialize(str);
					str = vObj.str;

					value[kObj.value]=vObj.value;
				}
				epos = str.indexOf('}');
				return {str:str.substring(epos+2),value:value};
			}
		}
		
	}
	return _unserialize(str).value;
}

$.dg = function(){
	var a = arguments,
		len = a.length,
		show = [];
	for(var i=0; i<len; i++){
			
			show.push($.serialize(a[i]));
	}
	switch($.browser){
		case 'Coship':
			$.each(show,function(v){Utility.println('sbory_debug==== '+v);});
			break;
		case 'iPanel':
		case 'iPanelAdvanced':
		case 'Sunniwell':
			$.each(show,function(v){iPanel.debug('sbory_debug==== '+v);});
			break;
		case 'Android':
		case 'Chrome':
		case 'WeiQiao':
		case 'Webdroid':
	//	case 'Chnavs':
			$.each(show,function(v){console.log('sbory_debug==== '+v);});
			break;
		case 'Hisu':
			$.each(show,function(v){System.debug('sbory_debug==== '+v);});
			break;
		case 'OTTSW':
			$.each(show,function(v){SWSystem.log('sbory_debug==== '+v);});
			break;
		default:
			break;
		
	}
}

$.typeOf = function(o){
	var type = typeof(o);
	
	if(type == 'object'){
		if(o==null){
			return 'null';
		}else if(o == Math){
			return 'math';
		}else if(o instanceof Array){
			return 'array';
		}else if(o instanceof Date){
			return 'date';
		}else if(o instanceof String){
			return 'string';
		}else if( o instanceof Number){
			return 'number';
		}else if( o instanceof RegExp){
			return 'reg';
		}
	}
	return type;
}

//将对象中的元素都执行一遍回调函数
$.each = function(obj,cb,args){
	args = args || {};
	var type = $.typeOf(obj);
	switch(type){
		case 'array':					
			var ret = [];
			for(var i = 0 , j = obj.length ; i < j ; i++){
				ret.push(cb(obj[i],i,args));
			}
			return ret;
		case 'object':
			var ret = {};
			for(var key in obj){
				ret[key] = cb(obj[key],key,args);
			}
			return ret;
		case 'number':
			for(var i=0;i<obj;i++){
				cb(i);	
			}
			break;
		default:
			return cb(obj,args);
	}
	
}

//序列化
$.serialize = function(o){
	var type = $.typeOf(o),
		serialize = $.serialize,
		f_push = 'push',
		f_join = 'join';
	switch(type){
		case 'string':					
			return '\''+$.replaceAll(o,'"','\\\'')+'\'';
		case 'number':
		case 'boolean':
		case 'function':
		case 'math':
		case 'reg':
		case 'xml':
			return o.toString();
		case 'null':
		case 'undefined':
			return type;
		case 'date':
			return '"'+$.date.format('YYYY-MM-dd HH:mm:ss',o)+'"';
		case 'array':
			var ret = [];
			for(var i=0,j=o.length; i<j; i++){
				ret[f_push](serialize(o[i]));
			}
			return '['+ret[f_join](',')+']';
		case 'object':
			var ret = [];
			for(var k in o){
				ret[f_push](k+':'+serialize(o[k]));
			}
			return '{'+ret[f_join](',')+'}';
		
	}
}

//反序列化
$.unserialize = function(o){
	return $.eval(o);
}

$.eval = function(o){
	if(typeof o!='string')return o;
	if(o.indexOf("`") > -1) o = $.replaceAll(o, "`", "'");
	o = o.replace(/[\r]?\n/g,'');
	var ret = "";
	if(o.length>0  && o.indexOf('<html><body></body></html>')<0){
		try{
			ret = eval('('+o+')');
		}catch(e){
	
		}
	}
	return ret;
}
//json格式转成xml格式
/*$.json2xml = function(o,key){
	var ret =[],
		type = $.typeOf(o),
		key = key || 'postData',
		keyFlag = (typeof key =='undefined')?false:true,
		f_json2xml = $.json2xml,
		f_push = 'push',
		p_length = 'length';

	if(arguments[p_length]<3)ret[f_push]('<?xml version=\'1.0\'?>');
	
	if(type =='array'){
		for(var l=o[p_length]-1; l>=0; l--){
			var tmp = o[l];
			ret[f_push](f_json2xml(o[l],key,1));
		}
			
	}else if(type =='object'){
		if(keyFlag){
			ret[f_push]('<'+key+'>');
		}
		for(var k in o){
			
				ret[f_push](f_json2xml(o[k],k,1));
			
		}
		if(keyFlag){
			ret[f_push]('</'+key+'>');
		}
	
		
	}else{
		if(type == 'undefined')return '';
		
		ret[f_push]('<'+key+'>'+o+'</'+key+'>');
		
	}
	
	return ret.join('');
}*/
//数字前面加上前缀
$.pad =  function (source, length) {
	return $.repeat('0',length-(source+'').length)+source;
}
$.repeat = function(source,length){
	return (new Array(length>-1?length+1:0)).join(source);
}
//字符串去掉重复字符或数组去掉重复子元素
$.distinct = function(o){
	var type =  typeof(o)=='string',
		a = type?o.split(''):o,
		ret=[],
		json = {};
	for(var i = 0,l=a.length; i < l; i++){
		if(!json[a[i]]){
			ret.push(a[i]);
			json[a[i]] = 1;
		}
	}
	
	return type?ret.join(''):ret;
}
//去除首尾字符
$.trim = function(s,c){
	c = (typeof c=='undefined')?'\\s':$.distinct(c);
    return s.replace(new RegExp('(^['+c+']*)|(['+c+']*$)','g'),'');  
}

//全替换
$.replaceAll = function(s,is,os){
	return s.split(is).join(os || '');
}


//移除数组或对象中某个值
$.remove = function(o,v,f){
	var ret = false,
		type = $.typeOf(o),
		f = f || 'KEY';
	if(type == 'array'){
		for(var i = 0 , j = o.length ; i < j ; i++){
			if( (f == 'VALUE' && v === o[i]) || (f=='KEY' && v==i)){
				ret = o.splice(i,1);
				break;
			}
		}
	}else if(type == 'object'){
		for(var k in o){
			if( (f == 'VALUE' &&v === o[k]) || (f=='KEY' && k==key)){
				delete o[k];
				break;
			}
		}
	}
	return ret;
}

//得到某个元素在数组中的索引值
$.indexOf = function(o,s,of){
	of = of || 0;
	for(var i = of,j= o.length  ; i<j ; i++){
		if(s == o[i]){
			return i;
		}
	}
	return -1;
}
$.lastIndexOf = function(o,s,of){	
	of = of || o.length -1;
	for(var i =of  ; i >=0 ; i--){
		if(s == o[i]){
			return i;
		}
	}
	return false;
}

//查找对象s是否是对象O的子元素
$.childOf = function(o,s){
	var type = $.typeOf(o),
		childOf = $.childOf;
	if(type=='array'){
		for(var i=o.length-1;i>=0;i--){
			if(o[i]===s || childOf(o[i],s)){
				return true;
			}
		}		
	}else if(type == 'object'){
		for(var k in o){
			if(o[k]===s){
				return true;
			}else if(childOf(o[k],s)){
				return true;
			}
		}
	}else if(o===s){
		return true;
	}
	return false;
}
//金额格式
$.money = function(n){
	var a = n.toString().split('');
	var ret = [];
	for(var i = a.length-1,j=1; i>=0;i--,j++){
		ret.push(a[i]);
		if(j%3==0 && i!=0){
			ret.push(',');
		}
	}
	return $.reverse(ret).join('');
}

//反转
$.reverse = function(o){
	var type = typeof o;
	if(type == 'string')o = o.split('');

	for(var i =0,l = o.length; i<Math.floor(l/2); i++){
		var tmp = o[i];
		o[i] = o[l-1-i] ;
		o[l-1-i] = tmp;
		
	}
	if(type == 'string')o = o.join('');
	return o;
	
}
//随机数
$.rand = function(num,offset){
	num = num || 1;
	offset = offset || 0;
	return Math.floor(Math.random()*num)+offset;
}

//批量给全局变量赋值
$.list = function(varsArr,valueArr){
	
	$.each(varsArr,function(v,k){
							window[v] = valueArr[k];
							})
	
}

$.empty = function(o){
	var type = $.typeOf(o);
	
	switch(type){
		case 'null':
		case 'undefined':
			return true;
			break;
		case 'string':
			if(o=='')return true;
			break;
		case 'array':
			if(o.length<1)return true;
			break;
		case 'object':
			for(var k in o){
				return false;
			}
			return true;
		default:
			return false;
	}
	return false;
}
//获取焦点

$.getFocus = function(varsArr,focusStr){
	focusStr = focusStr || 'focusStr';
	focusStr = $.url.query(focusStr);
	if(focusStr != null){
		var focusArr 	= focusStr.split(',');
			
		focusArr = $.each(focusArr,function(v){ return v-0});		
		$.list(varsArr,focusArr);
		return true;
	}
	return false;
}
//预加载图片
// Example:

// preloadImage( '01.gif'); 
// preloadImage(['01.gif','02.gif'])

$.preloadImage	= function(){ 
	var a = arguments,
		len = a.length,
		i = 0;
	for(;i<len;i++){
		var o = a[i];
		
		if(typeof o == 'string'){
			o = [ o ];
		}
		$.each(o,function(v){
				var img = new Image();
				img.src = v;
			});
	}
	
}
$.substr = function(str,len,s){
	if(typeof s != 'string') s = '...';
	var num = 0,
		j = 1,
		l = str.length;
	for(; j <= l; j++){
		if(str.charCodeAt(j-1) > 255) num+=2;
		else num++;
		if(num>=len){
			break;
		}
	}
	
	return j<l?str.substring(0,j)+s:str;
}

//split array by size
$.splitArr = function(arr,size){
	var len = arr.length,
		ret = [],
		i=0;
	while(i<len){
		ret.push(arr.slice(i,i+size));
		i += size;		
	}
	return ret;
}

$.joinArr = function(arr){
	var ret = [],
		i=0,
		len = arr.length;
	
	for(;i<len;i++){
		ret = ret.concat(arr[i]);
	}
	return ret;
}
//克隆简单对象
$.clone = function(obj){
	var ret ={};
	for(var k in obj){
		ret[k] =obj[k];
	}
	return ret;
}
//ajax({url:'/x.php',data:'a=b',type:'POST',sync:true,success:function(){},error:function(){},cache:{}}) 
$.ajax = function(obj){
	
	var url = obj.url || '',
		type = obj.type || 'GET',
		data = obj.data || null,
		async = typeof(obj.async) != 'boolean'?true:obj.async,
		success = obj.success || function(){},
		//error = obj.error || function(){},
		cache	= obj.cache || null,
		dataType = obj.dataType || 'text',
		timeout	= typeof(obj.timeout)=='undefined'?15000:obj.timeout,
		f = 'tf'+$.rand(10000000);
	if(url ==''){
		success('""');		
		return;
	}
	
	
	
	if(cache!=null && !$.empty(cache[url])){
		success(cache[url]);
		return;
	}
	
	

	if(url.indexOf('http://')==0 || url.indexOf('HTTP://')==0){
		var domain = location.host;
		if(domain.indexOf(':')<0){
			var port = location.port;
			if(port!='')domain +=port; 
		}
		var ajax_domain = url.substring(7,url.indexOf('/',7));
		
		if(ajax_domain !== domain){
			switch($.browser){
				case 'iPanel':
					break;
				/* //废弃海数AJAX私有接口
				case 'Hisu':
					if(window.HttpPool){
						$.HisuHttp.get(url, success);
						return;
					}*/
				default:
					obj.url = '/smart_hotel/webpage/jump.jsp?reqUrl='+$.url.encode(url);
					if( data != null){
						obj.data = 'postData='+$.url.encode(data);
					}
					$.ajax(obj); 
					return;
			}
		}
	}

	
	if(timeout>0)$.ajax[f] = -1;
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function(){
		if (xhr.readyState == 4){
			if(timeout>0){
				clearTimeout($.ajax[f]);
				delete $.ajax[f];
			}
			var p_status = xhr.status;
			if (p_status == 200 ){
				var ret = dataType =='text'?xhr.responseText:xhr.responseXML;
				if(cache!=null && type == 'GET')cache[url] = ret;
				success(ret);					
			}else{				
				success('""');				
			}	
		}
	}
	
	if(type == 'GET' && data !=null){
		url += (url.indexOf('?')>-1?'&':'?')+data; 
		data = null;
	}
	
	xhr.open(type,url,async);
	if(type == 'POST' ){
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded;text/xml; charset=utf-8");
	}
	xhr.send(data);
	if(timeout>0){
		$.ajax[f] = setTimeout(function(){		
			if(xhr && xhr.abort)xhr.abort();
			success('""');
		},timeout);
	}
}

$.each(['get','post'], function(v){
	$[v] = function(url){		
		var a = arguments,
			data = '',
			backcall = function(){},
			async = true,
			cache = null,
			type = v.toUpperCase(),
			timeout = 15000;
		for(var i=1,len = a.length; i<len; i++){
			var e = a[i],tmpType = typeof e;
			switch(tmpType){
				case 'string':
					if(e.indexOf('=')>-1)data =  e;
					else type = e;
					break;
				case 'boolean':
					async = e;
					break;
				case 'function':
					backcall = e;
					break;
				case 'object':
					cache = e;
					break;
				case 'number':
					timeout = e;
					break;
			}
		}
		$.ajax({url:url,data:data,type:type,success:backcall,async:async,cache:cache,timeout:timeout});
	}
});

/*海数WEBKIT盒子ajax不支持跨域，用他们私有的接口*/
/* //废弃海数AJAX私有接口
$.HisuHttp = {
	pool:[],
	requestObj : null,
	isBind : false,
	get : function(__url, __callback){
		if(window.HttpPool){
			var that = this;
			that.pool.push({url:__url,callback:__callback});	
			if(that.requestObj==null)that.request();
		}else{
			$.get(__url,__callback);
		}
	},

	bindFunc : function(handle, result, text){			
			$.HisuHttp.requestObj.callback(result?text:'""');
			$.HisuHttp.requestObj = null;
			$.HisuHttp.request();		
		},

	request : function(){
		var that = this;
		if(that.pool.length>0 && that.requestObj==null){
			that.requestObj = that.pool.shift();
			if(!that.isBind){
				HttpPool.onHttpFinish.connect(that.bindFunc);
				that.isBind=true;
			}
			var httpHandle = HttpPool.getHttpHandle();
			HttpPool.get(httpHandle,that.requestObj.url);
		}
	}
	
}*/
$.cookie = {

    get: function (name){
        var cookieName = encodeURIComponent(name) + "=",
            cookieStart = document.cookie.indexOf(cookieName),
            cookieValue = null;
            
        if (cookieStart > -1){
            var cookieEnd = document.cookie.indexOf(";", cookieStart)
            if (cookieEnd == -1){
                cookieEnd = document.cookie.length;
            }
            cookieValue = decodeURIComponent(document.cookie.substring(cookieStart + cookieName.length, cookieEnd));
        } 

        return cookieValue;
    },
    
    set: function (name, value, expires, path, domain, secure) {
        var cookieText = encodeURIComponent(name) + "=" + encodeURIComponent(value);
    
        if (expires instanceof Date) {
            cookieText += "; expires=" + expires.toGMTString();
        }
    
        if (path) {
            cookieText += "; path=" + path;
        }
    
        if (domain) {
            cookieText += "; domain=" + domain;
        }
    
        if (secure) {
            cookieText += "; secure";
        }
    
        document.cookie = cookieText;
    },
    
    unset: function (name, path, domain, secure){
        this.set(name, "", new Date(0), path, domain, secure);
    }

};

$.mem = {
	setVar : function(key,val,type){
		val = $.serialize(val);
		switch($.browser){
			
			case 'Coship':
				Utility.setEnv(key,val);
				break;
			case 'Android':
				if(System && System.setGlobalVar){
					if(val=="''" || val == '""')System.removeGlobalVar(key);
					else System.setGlobalVar(key,val);
				}else{
					if(val=="''" || val == '""')sessionStorage.removeItem(key);
					else sessionStorage.setItem(key,val);
				}
				break;
			case 'Chrome':
			case 'Webdroid':
			case 'Chnavs':
				if(val=="''" || val == '""')sessionStorage.removeItem(key);
				else sessionStorage.setItem(key,val);
				break;
			case 'Hisu':
				System.setWebEnv(key, val);
				break;
			case 'OTTSW':
				SWSystem.setConfig(key, val);
				break;
			case 'Sunniwell':
				iPanel.setGlobalVar(key,val);
				break;
			case 'iPanel':
			case 'iPanelAdvanced':
			case 'GXCATV':
				iPanel.setGlobalVar(key,val);
				break;
			case 'RocME':
				SysSetting.setEnv(key,val);
				break;	
			case 'WeiQiao':
				if(type == 1){
					if(val=="''" || val == '""') localStorage.removeItem(key);
					else localStorage.setItem(key,val);
				}
				else{
					if(val=="''" || val == '""')sessionStorage.removeItem(key);
					else sessionStorage.setItem(key,val);
				}
				break;
			case 'IE':
			default:
				$.cookie.set(key,val);
				break;
		}
	},
	
	getVar : function(key,type){
		var val;
		switch($.browser){
			case 'Coship':
				val = Utility.getEnv(key);
				break;
			case 'Android':
				if(System && System.getGlobalVar){
					val = System.getGlobalVar(key);
				}else{
					val = sessionStorage.getItem(key); 		
				}
				break;
			case 'Chrome':	
			case 'Webdroid':
			case 'Chnavs':
                val = sessionStorage.getItem(key); 			
				break;
			case 'Hisu':
				val = System.getWebEnv(key);
				break;
			case 'OTTSW':
				val = SWSystem.getConfig(key);
				break;
			case 'Sunniwell':
				val = iPanel.getGlobalVar(key);
				break;
			case 'iPanel':
			case 'iPanelAdvanced':
			case 'GXCATV':
				val = iPanel.getGlobalVar(key);
				break;
			case 'RocME':
				val = SysSetting.getEnv(key);
				break;
			case 'WeiQiao':
				if(type == 1) val = localStorage.getItem(key); 
				else val = sessionStorage.getItem(key); 	
				break;
			case 'IE':
			default:
				val = $.cookie.get(key);
				break;
		}
		return $.unserialize(val || '""');
	},

	unset : function(key){
		this.setVar(key,'');
	}
};

$.url ={
	href : location.href,
	search : location.search,
	//查询URL传递的参数值
	query : function ( key ,search) {
			search = search || this.search;
			var reg = new RegExp(
								'(^|&|\\?|#)' 
								+ key
								+ '=([^&]*)(&|\x24)', 
							'');
			var match = search.match(reg);
			if (match) {
				return match[2];
			}
			return null;
		},

	query2json : function(search){
			search = search || this.search;
			var f_indexOf = 'indexOf',
				f_split = 'split';
			if( search[f_indexOf]('=')<0){
				return {};
			}
			if(search[f_indexOf]('?')>-1 ){
				search = search.substr(1);
			}
			var searchArr = search[f_split]('&'),
				ret = {};

			for(var i=searchArr.length-1;i>=0;i--){
				var kv = searchArr[i][f_split]('='),
					k = kv[0],
					v = kv[1];
				ret[k] = v;
			}
			return ret;
		},

	json2query : function(o){
			var ret = [];
			for(k in o){
				ret.push(k+'='+o[k]);
			}
			return ret.join('&');
		},
	
	encode :function(url){
			return encodeURIComponent(url || this.href);
		},
	decode : function(url){
			return decodeURIComponent(url || this.href);
		},

	go	   : function(url){
			url = url || this.href;
			location.href = url;
	},
	
	getPath : function(url){
		url = url || this.href;
		return url.substring(0,url.lastIndexOf('/')+1);
	},
	
	getFile : function(url){
		url = (url || this.href).split('?')[0];
		return url.substring(url.lastIndexOf('/')+1);
	},

	getFileExt : function(url){
		url = this.getFile(url);
		return url.substring(url.lastIndexOf('.')+1);
	},

	getFilename : function(url){
		url = this.getFile(url);
		return url.substring(0,url.lastIndexOf('.'));
	},
	
	getDomain : function(url){
		url = url || this.href;
		var beginPos	= url.indexOf('//');
		if(beginPos < 0)return '';
		
		beginPos += 2;

		var	endPos		= url.indexOf('/',beginPos);
		if(endPos < 0)endPos = url.length; 
		return url.substring(beginPos,endPos);
	}
	
}


$.date = {
	currentDate : null,
	//传入字串格式化时间,字串格式：YYYY-MM-dd hh:mm:ss w
	format : function (pattern,source) {
		source = source || this.currentDate || new Date();
		
		if ('string' != typeof(pattern)) {
			return source.toString();
		}
		
		function replacer(patternPart, result) {
			pattern = pattern.replace(patternPart, result+'');
		}
		
		var pad     = $.pad,
			year    = source.getFullYear(),
			month   = source.getMonth() + 1,
			date2   = source.getDate(),
			hours   = source.getHours(),
			minutes = source.getMinutes(),
			seconds = source.getSeconds(),
			weekDay	= source.getDay();
			
		//星期日~星期六	
		var weekDays = ['\u661f\u671f\u65e5', '\u661f\u671f\u4e00', '\u661f\u671f\u4e8c', '\u661f\u671f\u4e09', '\u661f\u671f\u56db', '\u661f\u671f\u4e94', '\u661f\u671f\u516d'];
		var weekDays_EN = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
		replacer(/yyyy|YYYY/g, pad(year, 4));
		replacer(/yy|YY/g, pad(year.toString().slice(2), 2));
		replacer(/MM/g, pad(month, 2));
		replacer(/M/g, month);
		replacer(/dd/g, pad(date2, 2));
		replacer(/d/g, date2);
	
		replacer(/HH/g, pad(hours, 2));
		replacer(/H/g, hours);
		replacer(/hh/g, pad(hours % 12, 2));
		replacer(/h/g, hours % 12);
		replacer(/mm/g, pad(minutes, 2));
		replacer(/m/g, minutes);
		replacer(/ss/g, pad(seconds, 2));
		replacer(/s/g, seconds);
		replacer(/w|W/g, weekDays[weekDay]);
		replacer(/e|e/g, weekDays_EN[weekDay]);
		
		return pattern;

	},
	//传入日期字串，返回日期对象。日期字串格式：2011-11-22 12:34:56或2011-11-22或时间戳
	getDate : function(source,doffset,hoffset,moffset,soffset){
		doffset = doffset || 0;
		hoffset = hoffset || 0;
		moffset = moffset || 0;
		soffset = soffset || 0;
		if('string' == typeof( source)){
			var r = 'replace',
				dateArr	= source[r](' ','-')[r](/:/g,'-').split('-');
			if(dateArr.length==3){
				dateArr = dateArr.concat([0,0,0]);
			}
			$.each(dateArr,function(v,k){
				dateArr[k] = parseInt(v,10);
			});
			return new Date(dateArr[0],dateArr[1]-1,dateArr[2]+doffset,dateArr[3]+hoffset,dateArr[4]+moffset,dateArr[5]+soffset);
		}else if('number' == typeof( source)){
			var date = new Date(source);
			if(date.getTime() != source){
				date.setTime(source);
				if(date.getTime() != source){
					var secondTime = 1000,
						minuteTime = 60*secondTime,
						hourTime = 60*minuteTime,
						dayTime = 24*hourTime,

						days = Math.floor(source/dayTime),
						hours =  Math.floor(source%dayTime/hourTime),
						minutes = Math.floor(source%hourTime/minuteTime),
						seconds = Math.floor(source%minuteTime/secondTime);

					date = this.getDate('1970-01-01 08:00:00',days,hours,minutes,seconds);
				}
			}
			
			return this.getDate(date,doffset,hoffset,moffset,soffset);
		}else{
			source = source || this.currentDate || new Date();
			var year    = source.getFullYear(),
				month   = source.getMonth(),
				date2   = source.getDate(),
				hours   = source.getHours(),
				minutes = source.getMinutes(),
				seconds = source.getSeconds();
			
			return new Date(year, month, (date2+doffset), hours+hoffset, minutes+moffset, seconds+soffset);	
		}
	},
	//计算两个日期之间的毫秒数，日期字串格式：2011-11-22 12:34:56或2011-11-22
	duration : function(dest,source){
		if(typeof(dest) == 'undefined')return false;
		return $.date.getDate(dest,0)-$.date.getDate(source,0);
			
	},
	//倒计时
	countDown : function(dest,src){
		var f_floor = Math.floor,
			times	= Math.round($.date.duration(dest,src)/1000);
		
		if(times<0) return null;
		
		var days	= f_floor(times/(3600*24)),
		hours		= f_floor((times-(days*3600*24))/3600),
		minutes		= f_floor((times-(hours*3600)-(days*3600*24))/60);
		seconds 	= times%60;
		
		return {days:days,hours:hours,minutes:minutes,seconds:seconds};
	},
	clock : function(id,format,interval){		
		var that = $.date,
			clockStr = that.format(format);
		clearTimeout(that.timeout);
		interval = interval || 60;
		$(id).text(clockStr);
		that.timeout = setTimeout(function(){
			that.clock(id,format,interval);
		},interval*1000);
	},
	timeout : -1
	
};

(function(){
	var path = location.href;
	
	
	if(1){
		var gmtDate =new Date(397247);
		
		var utcYear =	gmtDate.getUTCFullYear(),
			utcMonth = gmtDate.getMonth(),
			utcDate = gmtDate.getUTCDate(),

			utcHours = gmtDate.getUTCHours(),
			utcMinutes = gmtDate.getUTCMinutes(),
			utcSeconds = gmtDate.getUTCSeconds();
		
		$.date.currentDate = new Date(utcYear,utcMonth,utcDate,utcHours+8,utcMinutes,utcSeconds);
		
		var interval = 1000;
		setInterval(function(){
			$.date.currentDate = new Date($.date.currentDate.getTime()+interval);
		},interval);
	}
		
		  
})();


/*******************   插件区域   ***************/

(function($){	
	var doc = document,
		browser = $.browser,
		plugs = $.plugs;

	if(browser != 'iPanel' && typeof iPanel == 'undefined') window.iPanel = {eventFrame:{},debug:function(){}};
	//按键映射
	plugs.keyMap = function(__event){		
		__event = __event || window.event;
		
		var keyCode = __event.keyCode || __event.which || __event.charCode,
			key = 'NULL',
			type = 1;  //1,按键消息; 2,系统消息.
		
		if(keyCode >=48 && keyCode<=57){
			key =  'NUMBER_'+(keyCode-48);			
		}else{		
			switch(keyCode){			
				case 1:
				case 38:
				case 87://hubei
					key =  'UP';
					break;
				case 2:
				case 40:
				case 83://hubei
					key = 'DOWN';
					break;
				case 3:
				case 37:
				case 65://hubei
					key = 'LEFT';
					break;
				case 4:
				case 39:
				case 68://hubei
					key = 'RIGHT';
					break;
				case 13:
					key = 'OK';
					break;
				case 339://2014-12-5 rockychen 由于湖南长沙的盒子都是通过退出键来返回的，所以兼容这种情况
				case 27:
				case 340:
				case 283:
				case 640://coship
				case 8://ie  //hubei 回看
					key = 'BACK';
					break;	
				//case 339:
				case 27://ie、coship、hubei
					key = 'EXIT';
					break;
				case 372:
				case 290:
				case 33://ie、coship,chrome,Hisu
				case 306://hubei
					key = 'PAGE_UP';
					break;
				case 373:
				case 291:
				case 34://ie、coship,chrome,Hisu
				case 307://hubei
					key =  'PAGE_DOWN';
					break;
				case 73://hubei
					key = 'SHOW';
					break;
				case 593://频道+				
				case 124://Hisu
					key =  'CHANNEL_UP';
					break;
				case 594:
				case 125://Hisu
					key = 'CHANNEL_DOWN';
					break;
				case 512:
				case 468://coship
				case 315://hubei
					key = 'HOMEPAGE';
					break;
				case 513:
				case 36://Hisu
				case 72://hubei
					key = 'MENU';
					break;
				case 595://音量+
				case 447://coship
				case 122://Hisu
				case 61://hubei
					key = "VOLUME_UP";
					break;
				case 596:
				case 448://coship
				case 123://Hisu
				case 45://hubei
					key = 'VOLUME_DOWN';
				
					break;
				case 567:
				case 457://coship
					key =  "INFO";
					break;
				case 802:
				case 598:
				case 407://coship
				case 128://Hisu
				case 86://hubei
					key = 'AUDIO_MODE';
					break;
				case 514:
				case 458://coship
				case 120://Hisu
				case 69://hubei
					key = 'EPG';
					break;
				case 521:
				case 77://hubei 短信
					key = 'MAIL';
					break;
				case 564:
				case 535://hubei
					key = 'AUDIO';
					break;
				case 519:
				case 97:
					key = 'SET';
					break;
				case 515:
					if($.browser=='RocME'){//hubei
						key = 'NEWS';
						break;
					}
					key = 'HELP';
					break;
				case 597://静音键
				case 449://coship
				case 121://Hisu
				case 67://hubei
					key = 'MUTE';
					break;
				case 768:
					key = 'F1';
					if($.browser == 'Sunniwell'){						
						var eventStr=Utility.getEvent();
						eval("eventJson="+eventStr);
						var typeStr=eventJson.type;
						if(typeStr == "EVENT_MEDIA_END"){							
							key = 'VOD_PROGRAM_END';
							type = 2;
						}
					}
					break;
				case 769:
					key = 'F2';
					break;
				case 770:
					key = 'F3';
					break;
				case 771:
					key = 'F4';
					break;
				case 832:
				case 442:
				case 112://chrome F1
				case 403://coship
				case 116://Hisu
				case 320://hubei
					key = 'RED';
					break;
				case 833:
				case 113://chrome F2
				case 404://coship
				case 117://Hisu
				case 321://hubei
					key = 'GREEN';
					break;
				case 834:
				case 114://chrome F3
				case 405://coship
				case 118://Hisu
				case 322://hubei
					key = 'YELLOW';
					break;
				case 835:
				case 115://chrome F4
				case 406://coship
				case 119://Hisu
				case 323://hubei
					key = 'BLUE';
					break;
				case 561://输入法
				case 35://coship
				case 16://ie key.shift
					key = 'IME';
					break;
				case 258:
					key = 'STANDBY';
					break;
				case 533://hubei 股票
					key = 'STOCK';
					break;
				case 802:
				case 598:
				case 86://hubei
					key = 'AUDIO_MODE';
					break;
				case 517://hubei 游戏
					key = 'GAME';
					break;
				case 562:				
					key = "BROADCAST";
					break;	
				case 563:
				case 313://hubei
					key = "TV";
					break;
				case 570:
				case 76://hubei
					key = "FAVORITE";
					break;
				case 515://hubei 资讯
					key = 'NEWS';
					break;

				/****************系统消息映射************/
				case 5202:
					key = "VOD_PREPAREPLAY_SUCCESS";
					type = 2;
					break;
				case 5203:
					key = "VOD_CONNECT_FAILED";
					type = 2;
					break;
				case 5205:
					key = "VOD_PLAY_SUCCESS";
					type = 2;
					break;
				case 5206:
					key = "VOD_PLAY_FAILED";
					type = 2;
					break;
				case 5210:
				case 40200://coship
					key = "VOD_PROGRAM_END";
					type = 2;
					break;
				case 5221:
					key = "VOD_START_FAILED";
					type = 2;
					break;
				case 5222:
					key = "VOD_START_BUFF";	
					type = 2;
					break;
				case 5400:
					key = "MP3_DOWNLOAD_SUCCESS";
					type = 2;
					break;
				case 5401:
					key = "MP3_DOWNLOAD_FAILED";
					type = 2;
					break;
				case 5402:
					key = "MP3_NO_MEMORY";
					type = 2;
					break;
				case 5403:
					key = "MP3_OPEN_SOURCE_FAILED";
					type = 2;
					break;
				case 5404:
					key = "MP3_PLAY_FINISH";
					type = 2;
					break;
				case 5410:
					key = "MP3_WORDS_DOWNLOAD_SUCCESS";
					type = 2;
					break;
				case 5412:
					key = "MP3_WORDS_SHOW";
					type = 2;
					break;
				case 5411:
					key = "MP3_WORDS_DOWNLOAD_FAILED";
					type = 2;
					break;
				case 6003:
					key = "DEVICE_USB_DELETE";
					type = 2;
					break;
				case 6004:
					key = "DEVICE_USB_UNAVAILABLE";
					type = 2;
					break;
				case 5550:
					key = "DVB_CABLE_CONNECT_SUCCESS";
					type = 2;
					break;
				case 5551:
					key = "DVB_CABLE_CONNECT_FAILED";
					type = 2;
					break;
				case 5974:
					key = "HTML_OPEN_FINISHED";
					type = 2;
					break;
				case 8001:
					key = "DVB_TUNE_SUCCESS";
					type = 2;
					break;
				case 8002:
					key = "DVB_TUNE_FAILED";	
					type = 2;
					break;
				case 8100:
					key = "DVB_EIT_SCHEDULE_READY";	
					type = 2;
					break;
				case 8101:
					key = "DVB_EIT_TIMEOUT";
					type = 2;
					break;
				case 8110:
					key = 'DVB_EIT_PF_READY';
					type = 2;
					break;
				case 8103:
					key = 'DVB_EIT_REFRESH_EVENT';
					type = 2;
					break;
				case 9500:
					key = 'BROWSER_PAUSE';
					type = 2;
					break;
				case 9501:
					key = 'BROWSER_RESUME';
					type = 2;
					break;
				default:
					key = 'NULL';
					type = 2;
					break;
			}
		}
		return {key:key,code:keyCode,type:type};
	}
	
	
	//按键处理
	plugs.keyProcess = function(key,keyObj){ return 1;};
	
	//按键默认处理
	plugs.keyDefaultProcess = function(keyObj){
		switch(keyObj.key){
			case 'EXIT':
				//location.href = 'http://192.168.1.31:8080/AppPlusPlatform/web/dvb/dvbplay.html';
				return 0;
				break;
				/*
			case 'MENU'://是为了兼容WeiQiao浏览器写的，如果其他浏览器不需要可以屏蔽掉
				//location.href = 'http://124.47.3.85:8080/AppPlusPlatform/index.htm';
				return 0;
				break;
				*/
			default:
				return 1;
				break;
		}
		return 0;
	};

	switch(browser){
		case 'iPanel':
		case 'iPanelAdvanced':
		case 'GXCATV':
			doc.onkeypress = doc.onirkeypress = function(event){
				var tmpObj = plugs.keyMap(event);
				tmpObj.p2 = -1;
				var ret = plugs.dialog.keyProcess(tmpObj) || 0;
				if(ret){
					ret =  plugs.keyProcess(tmpObj.key,tmpObj) || 0;
					if(ret)ret = plugs.keyDefaultProcess(tmpObj) || 0;
				}
				return ret;
			};	
			doc.onsystemevent = function(event){
				var tmpObj = plugs.keyMap(event);
				tmpObj.p2 = event.modifiers;
				var ret = plugs.dialog.keyProcess(tmpObj) || 0;
				if(ret){
					ret = plugs.keyProcess(tmpObj.key,tmpObj) || 0;
					if(ret)ret = plugs.keyDefaultProcess(tmpObj) || 0;
				}
				return ret;
			};	
			break;
		case 'Coship':
			doc.onkeypress = function(event){
				var tmpObj = plugs.keyMap(event);
				tmpObj.p2 = event.userInt;
				var ret = plugs.dialog.keyProcess(tmpObj) || 0;
				if(ret){
					ret = plugs.keyProcess(tmpObj.key,tmpObj) || 0;
					if(ret)ret = plugs.keyDefaultProcess(tmpObj) || 0;
				}
				if(ret ==0 && event && event.preventDefault)event.preventDefault();
			};
			break;
		
		case 'OTTSW':
			doc.onkeypress = function(event){
				var tmpObj = plugs.keyMap(event);
				tmpObj.p2 = -1;
				var ret = plugs.dialog.keyProcess(tmpObj) || 0;
				if(ret){
					ret = plugs.keyProcess(tmpObj.key,tmpObj) || 0;
					if(ret)ret = plugs.keyDefaultProcess(tmpObj) || 0;	
				}
				if(ret==0 && event && event.preventDefault)event.preventDefault();
			};
			break;
		case 'WeiQiao':
		case 'Android':
		case 'Hisu':
		case 'Chrome':
		case 'Webdroid':
		case 'Chnavs':
		case 'IE':
		default:
			doc.onkeydown = function(event){
				var tmpObj = plugs.keyMap(event);
				tmpObj.p2 = -1;
				var ret = plugs.dialog.keyProcess(tmpObj) || 0;
				if(ret){
					ret = plugs.keyProcess(tmpObj.key,tmpObj) || 0;
					if(ret)ret = plugs.keyDefaultProcess(tmpObj) || 0;
				}
				if(ret==0 ){
					if(event && event.preventDefault)event.preventDefault();
					else if(browser == 'IE')return false;
					return ret;
				}
			};	
			break;
	}

	//数据列表，用例见list.htm
	plugs.list = function(){
		var that = this;
		that._constructor = 'list';
		//列表数据长度
		that.dataLen= 0;
		//显示长度
		that.showLen = 0;
		
		//'t'(垂直top)或 'l'(水平left)或'2*4'(两行四列)
		that.showType	= 'l'; 
		
		//记录焦点位置
		that.focusPos	= 0;
		that.lastFocusPos = 0;
		//记录数据在列表中的位置
		that.dataPos	= 0;
		//记录当前显示的第一条数据在列表中的位置
		that.startPos	= 0;
		
		that.totalPage	= 0;
		that.currPage	= 0;
		
		that.showRows	= 1;
		that.showCols	= 1;
		
		that.pageStyle	= 0;
		//翻页时，当最后一页不满时，是否填满
	//	that.showFill	= false;
		//切换数据时，是否首尾循环
		that.showLoop	= false;
		//固定焦点
		that.focusFixed	= false;
		
		//焦点移动步长
		that.focusStep = [];
		//焦点所在初始化坐标
		that.focusPlace	= null;
		
		that.focusStyle	= 0; //0，焦点直接跳到期望处，1，焦点在focusMoveTime内移动到期望处，2，用2D属性webkitTransitionDuration滑动焦点
		that.focusDiv	= null;
		that.focusMoveTime = 0;
		
		that.focusStatus = false;
		that.readyStatus = false;
	}
	
	plugs.list.prototype = {
		haveData : function(){},
		
		noData : function(){},
		
		init : function(initJson){
			var that = this,
				p_dataLen = 'dataLen',
				p_showType = 'showType',
				p_showLen = 'showLen',
				//p_showFill = 'showFill',
				p_focusFixed = 'focusFixed',
				p_showLoop = 'showLoop',
				p_pageStyle = 'pageStyle',
				p_startPos = 'startPos',
				p_focusPos = 'focusPos',
				p_focusDiv = 'focusDiv',
				p_focusStyle = 'focusStyle',
				p_focusMoveTime = 'focusMoveTime',
				p_focusStep = 'focusStep',
				p_focusPlace = 'focusPlace';
				
			that[p_showLen]	= (initJson[p_showLen] || 0)-0;	
			that[p_dataLen]	= (initJson[p_dataLen] || 0)-0;
			
			that[p_showType]	= initJson[p_showType] || 'l';
			

			var dataPos = (initJson['dataPos'] || -1)-0,
				showType = that[p_showType],
				showLen = that[p_showLen];

			if(dataPos>0){
				that[p_startPos]	= Math.floor(dataPos/showLen)*showLen;
				that[p_focusPos]	= dataPos%showLen;
			}else{
				that[p_startPos]	= (initJson[p_startPos] || 0)-0;
				that[p_focusPos]	= (initJson[p_focusPos] || 0)-0;
			}
			that.lastFocusPos = that[p_focusPos];

			if(showType.indexOf('*')!=-1){
				var typeArr = showType.split('*');
				showType = that[p_showType] = '*';
				
				that.showRows = typeArr[0]*1;
				that.showCols = typeArr[1]*1;
			}
			
			//that[p_showFill]	= initJson[p_showFill] || false;
			
			that[p_focusFixed]	= initJson[p_focusFixed] || false;
			if(that[p_focusFixed]){
				that[p_showLoop] = true;
			}
			var showLoop = initJson[p_showLoop];
			that[p_showLoop]	= typeof(showLoop)!='undefined'?showLoop: that[p_showLoop];
			
			that[p_pageStyle]	=  initJson[p_pageStyle] || 0;
			if(showType=='*'){
				that[p_pageStyle] = 1;
			}
			if(that[p_pageStyle] == 1){
				that[p_focusFixed] = false;
				that[p_showLoop] = false;
				
				that.totalPage = Math.ceil(that[p_dataLen]/showLen);
				that.currPage		= Math.floor(that[p_startPos]/showLen)+1;
			}
			
			
			
			var fDiv = initJson[p_focusDiv];
				
			that[p_focusDiv]	= (typeof  fDiv != 'undefined')?(fDiv._constructor == 'slip'?fDiv:$(fDiv)):null;
			
			var focusDiv = that[p_focusDiv];
			if(focusDiv != null){
				that[p_focusStyle]	= initJson[p_focusStyle] || 0;
				var focusStyle = that[p_focusStyle];
				
				
				if(focusDiv._constructor=='slip'){
					that[p_focusStyle] = 1;
				}else{
					that[p_focusStyle] = focusStyle;
					focusStyle = that[p_focusStyle];
					if(focusStyle	!= 0){
						that[p_focusMoveTime] = initJson[p_focusMoveTime] || 200;
						if(focusStyle==1){
							if(that[p_focusMoveTime] >= 100)that[p_focusMoveTime]=30;
							var tmpType = (showType == 'l'|| showType == 'left')?'left':'top';
							that[p_focusDiv] = new $.plugs.slip();
							that[p_focusDiv].init({id:fDiv,moveType:tmpType,moveTime:that[p_focusMoveTime]});
						}
						
					}
				}
				
				
				var focusStep	= initJson[p_focusStep],					
					stepType	= typeof focusStep,
					focusPlace  = initJson[p_focusPlace];
					
				that[p_focusPlace] = focusPlace ;	
				
				if(showType == '*'){
					that[p_focusStep] = focusStep;
				}else{
					that[p_focusStep][0] = focusPlace;
					if(stepType == 'object'){
						var fsLen = focusStep.length;
						for(var i=1;i<showLen;i++){
							var tmpStep = i-1<fsLen?focusStep[i-1]:focusStep[fsLen-1];
							that[p_focusStep][i] = that[p_focusStep][i-1]+tmpStep;
						}
					}else if(stepType == 'number'){
						for(var i=1;i<showLen;i++){
							that[p_focusStep][i] = that[p_focusStep][i-1]+focusStep;
						}
						
					}
				}
				
			}
			
			
			that.readyStatus	= true;
			
			that.showList();
			
		},
		
		showList : function(){
			var that = this;
			if(!that.readyStatus)return;
			var dataLen = that.dataLen;
			if(dataLen < 1)return;
			var showLen = that.showLen,
				 i		= 0,
				 p_startPos = 'startPos',
				 p_focusPos = 'focusPos',
				 f_haveData = 'haveData';
				 
				 
			that[p_startPos] = (that[p_startPos]+dataLen)%dataLen;
			
			var startPos = that[p_startPos],
				showLoop = that.showLoop;
				
			for(; i < showLen; i++){
				if(showLoop){
					that[f_haveData](i,(i+startPos+dataLen)%dataLen);
				}else if(showLen+startPos >= dataLen && dataLen>that.showLen && that.pageStyle != 1){	
					that[p_startPos] = dataLen-showLen;
					startPos = that[p_startPos];
					that[f_haveData](i,i+startPos);
				}else{
					if((i+startPos)<dataLen)that[f_haveData](i,i+startPos);else that.noData(i,i+startPos);
				}
			}
			
			that.dataPos = (startPos+that[p_focusPos]+dataLen)%dataLen;
			if(that.focusStatus)that.focus();
		},
		
		
		focus : function(){
			
			var that = this;
			if(!that.readyStatus)return;
			that.focusStatus = true;
			var focusDiv = that.focusDiv; 
			if(focusDiv == null )return;
			
			var showType = that.showType,
				focusPos = 	that.focusPos,
				focusStep = that.focusStep,
				focusPlace = that.focusPlace,
				lastFocusPos = that.lastFocusPos,
				focusMoveTime = that.focusMoveTime,
				focusStyle = that.focusStyle,
				p_left = 'left',
				p_top = 'top',
				p_showCols = 'showCols',
				_f = Math.floor;
			
			if(focusStyle != 1){ //焦点非slip对象
				focusDiv.show();
				if(focusStyle == 2 && (browser == 'iPanel' || browser == 'Chrome' || browser == 'iPanelAdvanced')){
					focusDiv.webkit('0ms');
					focusDiv.webkit(focusMoveTime + 'ms');
				}
				
				if(showType=='*'){
					var focusLeft,focusTop;
					if(!focusStep.down){
					 	focusLeft	= focusPlace[p_left]+_f(focusPos%that[p_showCols])*focusStep[p_left],
						focusTop	= focusPlace[p_top]+_f(focusPos/that[p_showCols])*focusStep[p_top];
					}else{
						focusLeft	= focusPlace[p_left]+_f(focusPos/that[p_showCols])*focusStep[p_left],
						focusTop	= focusPlace[p_top]+_f(focusPos%that[p_showCols])*focusStep[p_top];
					}
					focusDiv.left(focusLeft);
					focusDiv.top(focusTop);
				}else {		
					var type = (showType == 'l'|| showType == p_left)?p_left:p_top;
					focusDiv[type](focusStep[focusPos]);
				}
				
				
			}else{ //焦点为slip 对象
					focusDiv.moveObj.show();
					if(lastFocusPos!==focusPos){						
							focusDiv.moveStart(focusStep[lastFocusPos],focusStep[focusPos]);						
					}
					
				
			}
		},
		
		blur : function(){
			var that = this;
			if(!that.readyStatus)return;
			that.focusStatus = false;
			var focusDiv = that.focusDiv; 
			if(focusDiv != null){
				if(that.focusStyle == 1)focusDiv.moveObj.hide();
				else focusDiv.hide();
			}
		},
		
		
		changeList : function(num){
			var that = this,
				dataLen = that.dataLen;
			if(!that.readyStatus || dataLen < 1)return;
			
			var	showLen = that.showLen,
				showLoop = that.showLoop,
				showType = that.showType,
				focusPos = that.focusPos,
				
				tmpFocusPos = focusPos+num,
				p_startPos = 'startPos',
				p_dataPos = 'dataPos',
				p_focusPos = 'focusPos',
				p_lastFocusPos = 'lastFocusPos',
				f_showList = 'showList',
				f_focus = 'focus',
				f_indexOf = 'indexOf',
				tmpStartPos = that[p_startPos] + num;
			
			if(that.focusFixed){
				that[p_startPos] = (tmpStartPos+dataLen)%dataLen;
				that[f_showList]();			
			}else if((tmpFocusPos >= 0 && tmpFocusPos < showLen ) && (tmpFocusPos+that[p_startPos] < dataLen || showLoop ) ){	
				
				that[p_lastFocusPos] = focusPos;
				that[p_focusPos] = tmpFocusPos;
				that[f_focus]();
				
				that[p_dataPos] = (that[p_startPos]+tmpFocusPos+dataLen)%dataLen;
			
			}else if(showLoop){
					that[p_startPos] = (tmpStartPos+dataLen)%dataLen;
					that[f_showList]();					
			}else{
				
				var pageStyle = that.pageStyle,
					dataPos = that[p_dataPos],
					tmpDataPos = dataPos + num;
					
				if((tmpDataPos < 0 || tmpDataPos >= dataLen) && pageStyle != 1){
					that[p_lastFocusPos] = focusPos;
					if(tmpDataPos < 0){
						that[p_startPos] = dataLen>showLen?dataLen-showLen:0;
						that[p_focusPos] = dataLen>showLen?showLen-1:dataLen-1;
					}else{
						that[p_startPos] = 0;
						that[p_focusPos] =0;
					}
					that[f_showList]();
				}else if(tmpDataPos >= 0 && tmpDataPos < dataLen && showType!='*'){
					if(that.pageStyle == 1 && (tmpFocusPos<0 || tmpFocusPos>showLen-1)){
						
						that[p_lastFocusPos] = focusPos;
						that[p_focusPos] = num>0?0:showLen-1;
						that.changePage(num,1);
						
					}else{
						that[p_lastFocusPos] = focusPos;
						that[p_startPos] = tmpStartPos;
						that[f_showList]();
					}
					
				}else if(showType=='*' && Math.abs(num)>1 && tmpDataPos >= dataLen){
					
					var cols = that.showCols,
						M_F = Math.floor;
					if(M_F(dataPos/cols)!=M_F((dataLen-1)/cols) && that.currPage == that.totalPage){
						that[p_lastFocusPos] = focusPos;
						that[p_dataPos] = dataLen-1;
						
						that[p_focusPos] = that[p_dataPos] - that[p_startPos] ;					
						that[f_focus]();
						
					}
					
				}
			}
		},
		
		
		changePage : function(num,flag){			
			var that = this,
				dataLen= that.dataLen;
			
			if(!that.readyStatus || dataLen < 1)return;
			
			var showLen = that.showLen,
				p_focusPos = 'focusPos',
				p_currPage = 'currPage',
				p_startPos = 'startPos',
				f_showList = 'showList';
			
			if(that.pageStyle == 0){
				if(that.focusFixed){
					that[p_startPos] += num*showLen;
					that[f_showList]();
				}else if(that.showLoop){
					that[p_startPos] += num*showLen;
					that[p_focusPos] = 0;
					that[f_showList]();
				}else{
					var tmpStartPos = that[p_startPos]+num*showLen;			
					that[p_startPos] = tmpStartPos<0?0:((tmpStartPos >= dataLen)?that[p_startPos]:tmpStartPos);
					that[f_showList]();
				}
			}else {
				var tmpPage = that[p_currPage] + num;
				if(tmpPage > 0 && tmpPage <= that.totalPage){					
					flag = flag || 0;
					that[p_currPage] = tmpPage;
					that[p_startPos] = (tmpPage-1)*showLen;
					if(flag==0)that[p_focusPos] = 0;
					that[f_showList]();
				}
			}
		},
		
		isLimit:function(source){
			var that = this;
			if(that.showType=='*'){
				var focusPos = that.focusPos,
					cols = that.showCols,
					dataPos = that.dataPos,
					dataLen = that.dataLen;
				switch(source){
					case 'top':
					case 't':
						if(focusPos<cols)return true;
						break;
					case 'left':
					case 'l':
						if(focusPos%cols==0)return true;
						break;
					case 'right':
					case 'r':
						if(focusPos%cols==cols-1 || dataPos == dataLen-1)return true;
						break;
					case 'bottom':
					case 'b':
						var M_F = Math.floor;
						if(focusPos>=cols*(that.showRows-1) || M_F(dataPos/cols) == M_F((dataLen-1)/cols))return true;
						break;
						
				}
				return false;
			}
		}
	};
	
	//滑动焦点,每次移动剩下的moveStep分之一.用例见list.htm 
	plugs.slip = function(){
		var that = this;
		that._constructor	= 'slip';
		
		that.moveObj 		=  null;
		
		that.currPlace = 0;
		that.destPlace = 0;
		
		that.moveType		= 'left'; //left：横向，top:纵向。
		
		that.moveTimer 		= -1;
		
		that.moveStep		= 3;
		
		that.moveTime		= 30; //第次移动间隔时间，单位毫秒。
		that.direction		= 1;
	};
	
	plugs.slip.prototype = {
		init : function(data){ //id,moveType,moveStep,moveTime
			var that = this,
				p_moveObj = 'moveObj',
				p_keys = ['moveType','moveStep','moveTime'];
			
			
			that[p_moveObj]	= $(data.id);
			
			$.each(p_keys,function(v){ that[v] = data[v] || that[v];});
			
		},
		
		
		moveStart: function(currPlace,destPlace){
			var that = this;
			clearTimeout(that.moveTimer);
			that.currPlace = currPlace;
			that.destPlace = destPlace;
			that.moveObj[that.moveType](currPlace);
			that.direction = destPlace>currPlace?1:-1;
			
			that.onMoveStart();
			that.moving();
		},
		
		moving : function(){	
			var that = this,
				p_currPlace = 'currPlace',
				moveObj = that.moveObj,
				moveType = that.moveType,
				currPlace = that[p_currPlace],
				destPlace = that.destPlace,
				direction = that.direction,
				tmpStep = Math.ceil(Math.abs(destPlace-currPlace)/that.moveStep);	
				
			clearTimeout(that.moveTimer);
			that[p_currPlace] += direction*tmpStep;
			currPlace = that[p_currPlace];
			
			//iPanel.debug('ce err that[p_currPlace]===='+that[p_currPlace] +','+ destPlace + ','+tmpStep);
			
			if( (direction>0 && currPlace >=destPlace) || (direction<0 && currPlace<=destPlace)){
				that[p_currPlace] = destPlace;
				that.onMoveEnd();
				
			}else{
				
				that.moveTimer = setTimeout(function(){that.moving();},that.moveTime);
			}
			moveObj[moveType](that[p_currPlace]);
			
			
		},
		
		onMoveStart : function(){},
		
		onMoveEnd : function(){}
		
	}
	//滚动条,用例见list.htm
	plugs.scroll = function(){
		var that = this;
		that._constructor	= 'scroll';
		that.scrollObj	= null;
		that.bgObj		= null;
		that.scrollBody	= null;
		that.total 		= 0;
		that.top		= 0;
		that.step		= 0;
		that.scrollType	= 0;//0,跳动；1，平滑;2,2D
		that.lastPos	= 0;
		that.moveTime	= 200;
		that.forward	= 't'; //t:垂直；l:水平
	}
	plugs.scroll.prototype = {
		init : function(initData){
			var that = this,
				scrollId = initData.scrollId,
				p_scrollType = 'scrollType',
				p_scrollObj = 'scrollObj',
				p_bgObj = 'bgObj',
				p_total = 'total',
				p_moveTime = 'moveTime',
				p_scrollBody = 'scrollBodyId',
				p_forward = 'forward';
				
			if(scrollId._constructor == 'slip'){
				that[p_scrollType] = 1;
			}else{
				that[p_scrollType]	= initData[p_scrollType] || that[p_scrollType]; 
				that[p_moveTime] = initData[p_moveTime] || that[p_moveTime];
				
				if(that[p_scrollType]!=1){
					that[p_scrollObj] = $(scrollId);
				}else{
					that[p_scrollObj] = new plugs.slip();
					that[p_scrollObj].init({id:scrollId,
								   moveType:'top',
								   moveTime:that[p_moveTime]});
					
				}
			}
			
			that[p_bgObj]		= $(initData.scrollBgId);
			that[p_total] 		= initData[p_total];
			that[p_scrollBody]	= $(initData[p_scrollBody]);
			that[p_forward]	= initData[p_forward] || that[p_forward]; 
			
			
			
			
			
			var bgObj = that[p_bgObj],
				scrollObj = that[p_scrollObj],
				total = that[p_total],
				p_height = that[p_forward]=='t'?'height':'width',
				p_top = that[p_forward]=='t'?'top':'left';
			
			if(scrollObj == null || bgObj==null || that[p_total] < 2)return;
			
			
			that.step = bgObj[p_height]()/total;
			that[p_scrollBody][p_height](that.step);
			
			if(that[p_scrollType]!=1){				
				that[p_top] = scrollObj[p_top]();
				scrollObj.show();
			}else{	
				var moveObj = scrollObj.moveObj;
				moveObj[p_height](that.step);
				moveObj.show();
				that[p_top] = moveObj[p_top]();
			}
			bgObj.show();	
			
		},
		scroll : function(pos){
			var that = this,
				bgObj = that.bgObj,
				scrollObj = that.scrollObj,
				p_lastPos = 'lastPos',
				p_height = that.forward=='t'?'height':'width',
				p_top = that.forward=='t'?'top':'left',
				tmpPos = pos-1,
				that_step = that.step,
				that_top = that[p_top],
				scrollType = that.scrollType;
			if(scrollType==1){
				var moveObj = scrollObj.moveObj;
				
				if(that[p_lastPos] != tmpPos){
					that[p_lastPos] = tmpPos;
					scrollObj.moveStart(that_top + that_step*(that[p_lastPos]),that_top + that_step*(tmpPos));
				}
				
			}else{
				
				var top = that_top + that_step*(tmpPos);
				if(scrollType==2 && ( browser =='iPanel'|| browser == 'Chrome' || browser == 'iPanelAdvanced')){
					scrollObj.webkit('0ms');
					scrollObj.webkit(that.moveTime+'ms');
				}
				scrollObj[p_top](top);
			}
		},
		show : function(){
			var that = this,
				scrollObj = that.scrollObj;
			if(that.scrollType!=1)scrollObj.webkit(that.moveTime+'ms').show();
			else scrollObj.moveObj.show();
			
			that.bgObj.show();
		},
		hide : function(){
			var that = this,
				scrollObj = that.scrollObj;
			if(that.scrollType!=1)scrollObj.webkit(0).hide();
			else scrollObj.moveObj.hide();
			
			that.bgObj.hide();
		}
	}
	
	//输入框,用例见input.htm
	plugs.input = function(){
		var that = this;
		that._constructor	= 'input';
		that.input		= null;
		
		that.showType	= '';  //text , password
		that.imeType	= 'number';	//letter
		that.cursor		= '';
		that.blank		= '';
		that.pwdMark	= '';
		
		that.maxLen		= 0;
		that.imgHeight	= 0;
		
		that.cursorPos	= 0;
		that.inputStr 	= [];
		that.startPos	= 0;
		that.letterList	= [
							['0',' ','@','?','&'],
							['1','/','.',':','#'],
							['a','b','c','2','A','B','C'],
							['d','e','f','3','D','E','F'],
							['g','h','i','4','G','H','I'],
							['j','k','l','5','J','K','L'],
							['m','n','o','6','M','N','O'],
							['p','q','r','7','s','P','Q','R','S'],
							['t','u','v','8','T','U','V'],
							['w','x','y','9','z','W','X','Y','Z']
							
						   ];
		that.letterPos	= 0;
		that.letterTimeout = -1;
		
		that.lastKey	= -1;
		that.letterMode = 0; 
		that.focusStatus = false;
		that.readyStatus	= false;
	}
	
	plugs.input.prototype = {
		init : function(data){
			var that		= this,
				p_input		= 'input',
				p_showType	= 'showType',
				p_imeType	= 'imeType',
				p_maxLen	= 'maxLen',
				p_cursor	= 'cursor',
				p_blank		= 'blank',
				p_imgHeight = 'imgHeight',
				p_pwdMark	= 'pwdMark',
				p_inputWidth= 'inputWidth',
				p_letterMode= 'letterMode';
			that[p_input]		= $(data.id);
			if(that[p_input].e == null)return;
			
			that[p_showType] 	= data[p_showType] || 'text';
			if(that[p_showType] != 'password'){
				that[p_imeType]	= data[p_imeType] || 'number';//'letter'
			}
			
			that[p_maxLen]		= data[p_maxLen] || 5;
			that[p_cursor]		= data[p_cursor] || 'focus.gif';
			that[p_blank]		= data[p_blank] || '';
			that[p_imgHeight] = data[p_imgHeight] || 30;
			that[p_pwdMark]	= data[p_pwdMark] || '*';
			var o_input = that[p_input];
			that[p_inputWidth] = data[p_inputWidth] || o_input.width() || o_input.offsetWidth() || 200;
			
			that.letterLim = 0;
			that[p_letterMode] = data[p_letterMode] || 0;
			if(that[p_letterMode]==1){
				that.letterList = [
									['0',' '],
									['1','/'],
									['a','b','c','2'],
									['d','e','f','3'],
									['g','h','i','4'],
									['j','k','l','5'],
									['m','n','o','6'],
									['p','q','r','s','7'],
									['t','u','v','8'],
									['w','x','y','z','9']
								];
			}
			that.readyStatus = true;
		},
		
		getInput : function(_char){
			var that = this;
			if(!that.readyStatus)return;
			var key = 1*_char,
				p_lastKey = 'lastKey',
				_l = 'length',
				p_cursorPos = 'cursorPos',
				p_letterPos = 'letterPos',
				p_letterTimeout = 'letterTimeout',
				_sp = 'splice',
				inputStr = that.inputStr,
				lastKey = that[p_lastKey],
				imeType = that.imeType;
				
			if(that.input.e == null || (inputStr[_l] >= that.maxLen )){//&& this.cursorPos >= this.length
				
				
				if(imeType == 'number')return;
				else if( lastKey > -1 && lastKey == key){}
				else if(imeType == 'letter'){return;}
				else return;
			}
			
			if(imeType == 'number'){
				inputStr[_sp](that[p_cursorPos],0,_char);//this.cursorPos < this.length?1:0
				
				that[p_cursorPos] ++;
			}else {
				clearTimeout(that[p_letterTimeout]);
				
				var	letters = that.letterList[key];
				that[p_letterPos] %= letters[_l];
				
				if( lastKey > -1 && lastKey == key){
					inputStr[_sp](that[p_cursorPos]-1,1,letters[that[p_letterPos]]);	
				}else{
					that[p_letterPos] = 0;
					inputStr[_sp](that[p_cursorPos],0,letters[that[p_letterPos]]);//this.cursorPos < this.length?1:0
					that[p_cursorPos] ++;
				}
				that[p_letterPos]++;	
				
						
				that[p_letterTimeout] = setTimeout(function(){
														 that[p_lastKey] = -1;
														 that[p_letterPos] = 0;
														 },800);
				
				that[p_lastKey] = key;
				//that[_l]	 = inputStr[_l];
				var str,tmpLen,
					p_startPos = 'startPos',
					p_letterLim = 'letterLim',
					tmpCursorPos = that[p_cursorPos],
					tmpStartPos = that[p_startPos],
					tmpLetterLim = that[p_letterLim],
					tmpInputWidth = that.inputWidth,
					inputObj = that.input,
					combiString = 'combiString';
			
				if(tmpCursorPos == inputStr[_l] ){		
					var count = 0;
					while(true){
						str = that.getInputStr().substring(tmpStartPos);

						tmpLen = inputObj[combiString](str,tmpInputWidth)[_l];
						
						if(str[_l]>tmpLen){
							count++;
							that[p_startPos] ++;
							if(count>1)break;
							continue;
						}
						break;
					}
					
					that[p_letterLim] = tmpLen;	
					
				}else if(tmpCursorPos>=tmpStartPos && tmpCursorPos <= tmpStartPos+tmpLetterLim){
					
					str = that.getInputStr().substring(tmpStartPos);
					tmpLen = inputObj[combiString](str,tmpInputWidth)[_l];
					if(str[_l]>tmpLen && tmpCursorPos > tmpStartPos+tmpLen){
						
						that[p_startPos] ++;
						
						
					}
					
					that[p_letterLim] = tmpLen;
					
						
				}else if(tmpCursorPos > tmpStartPos+tmpLetterLim){
				
					str = $.reverse(that.inputStr.slice(0,tmpCursorPos)).join('');
					tmpLen = inputObj[combiString](str,tmpInputWidth)[_l];
				
					
					that[p_letterLim] = tmpLen;	
					that[p_startPos] = tmpCursorPos-that[p_letterLim];
					
				}
				
			
				that.upper();
				
			}
			that.showInput();
		},
		
		backSpace : function(){
			var that = this,
				_l = 'length',
				p_cursorPos = 'cursorPos',
				cursorPos = that[p_cursorPos],
				inputStr = that.inputStr;
			if(!that.readyStatus)return;
			if(that[_l] < 1)return;
			
			if(cursorPos == 0)return;
			
			cursorPos = --that[p_cursorPos] ;
			 
			inputStr.splice(cursorPos,1);

			if(that.imeType == 'letter'){
				if(that.startPos>0 && cursorPos==that.startPos)that.startPos--;
			}	
			
			that.showInput();
		},
		
		showInput : function(){
			var that = this;
			if(!that.readyStatus)return;
			var imgHeight = that.imgHeight,
				inputStr = that.inputStr,
				inputObj = that.input,
				_l = 'length',
				_p = 'push',
				tmpCursorPos = that.cursorPos,
				cursorTag = '<img src="'+that.cursor+'" width="2" height="'+imgHeight+'" />',
				blankTag = '<img src="'+that.blank+'" width="1" height="'+imgHeight+'" />',
				html = [], //IE上不加&nbsp;输入框没有内容时，光标偏上，没有居中
				len = inputStr[_l],
				isPassword = that.showType=='password'?true:false,
				pwdMark = that.pwdMark;
				
			if(len>=0 && tmpCursorPos >=0 ){
				if(len==0)
					html[_p]('');
				
				html[_p]( tmpCursorPos == 0?cursorTag:blankTag);
				
				if(that.imeType == 'letter'){
					
					var maxLen = that.letterLim;
					
					for(var i =0;i<maxLen;i++){
						var tmp = that.startPos+i;
						if(tmp<len){
							html[_p](isPassword?pwdMark:inputStr[tmp]);				
							
							if(tmpCursorPos == tmp+1)html[_p](cursorTag);
						}
					}
					
				}else{
					for(var i=0;i<len;i++){
						html[_p]( isPassword?pwdMark:inputStr[i]);					
						if(tmpCursorPos == i+1)html[_p]( cursorTag);
					}
				}
				
				inputObj.html(html.join(''));
			}else{
				var text = that.getInputStr();
				inputObj.text(isPassword?$.repeat(pwdMark,text.length):text);
			}
		},
		
		moveCursor : function(num){
			var that = this;
			if(!that.readyStatus)return;
			var p_length = 'length',
				tmpPos = that.cursorPos + num,
				imeType = that.imeType,
				len = that.inputStr[p_length];
				
			if(tmpPos < 0 || tmpPos > len){
				return;
			}
			that.cursorPos = tmpPos;
			
			if(imeType == 'letter'){
				var startPos = that.startPos,
					cursorPos = that.cursorPos,
					maxLen = that.letterLim,
					pos = Math.floor(maxLen/2);
				
				if(cursorPos == startPos && startPos>0){
					that.startPos = startPos>pos?startPos-pos:0;
					
				}else if(cursorPos > startPos+maxLen ){
					that.startPos ++;
					
				}
				
				that.letterLim = that.input.combiString(that.getInputStr().substring(that.startPos),that.inputWidth)[p_length];
				if(cursorPos > that.startPos+that.letterLim)that.cursorPos =  that.startPos+that.letterLim;
			}
			that.showInput();
		},
		
		getInputStr : function(){
			var that = this;
			if(!that.readyStatus)return;
			return that.inputStr.join('');
		},
		
		initInputStr : function(__inputStr){
			var that = this;
			if(!that.readyStatus)return;
			that.inputStr = $.empty(__inputStr)?[]:__inputStr.split('');
			var text = that.getInputStr();
			that.input.text(that.showType=='password'?$.repeat(that.pwdMark,text.length):text);
		},
		
		focus : function(){
			var that = this,
				_l = 'length';
			if(!that.readyStatus)return;
			that.focusStatus = true;
			len = that.inputStr[_l];
			
			that.cursorPos = len;
			
			if(that.imeType == 'letter'){
				that.letterLim = that.input.combiString($.reverse(that.getInputStr()),that.inputWidth)[_l];
			
				that.startPos = that.inputStr[_l]-that.letterLim;
			}
			
			that.showInput();
		},
		
		blur : function(){
			var that = this;
			if(!that.readyStatus)return;
			that.focusStatus = false;
			that.cursorPos = -1;
			that.showInput();
		},
		
		imeToggle : function(){
			var that = this,
				p_imeType = 'imeType';
			if(!that.readyStatus)return;
			if(that.showType == 'password')return;
			that[p_imeType] = that[p_imeType]=='number'?'letter':'number';
		}
		,
		upper:function(){
			var that = this,
				p_inputStr = 'inputStr';
			if(that[p_inputStr].length<1)return ;
			if(that.letterMode==1){				
				that[p_inputStr] = that.getInputStr().toUpperCase().split('');
			}
		}
	}
	
	//正文内容分页
	plugs.page = function(){
		var that = this;
		that.pagePostions = [];	
		that.currPage = 1;
		that.totalPage = 1;
		that.contenter = null;
		that.scrollType = 0;
	}
	
	plugs.page.prototype = {
		init:function(data){
			var that = this,
				p_pheight = 'pageHeight',
				p_moveStep = 'moveStep',
				p_contenter = 'contenter';
				
			that[p_pheight] = data[p_pheight] || 300;
			that[p_moveStep] = data[p_moveStep];
			var cid = data.contentId;
			if(typeof cid == 'object' && cid._constructor=='slip'){
				that[p_contenter] = cid;
				that.scrollType = 1;
			}else{
				that[p_contenter] = $(cid);
			}
			var callback = data.callback || function(){};
			if(that.contenter != null){
				setTimeout(function(){
						that.getPagePostions();
						if(typeof callback == 'function'){
							callback();
						}
					},500);
			}
		},
		reset :function(){
			var that = this,
				contenter = that.contenter,
				p0 = that.pagePostions[0];
			that.currPage = 1;
			
			if(that.scrollType==1)contenter.moveObj.top(p0);
			else contenter.top(p0);
		},
		
		getPagePostions:function(){
			var that = this,
				contenter = that.contenter,
				totalHeight = that.scrollType==1?contenter.moveObj.offsetHeight():contenter.offsetHeight(),
				pageHeight = that.pageHeight,
				moveStep	= that.moveStep,
				postions = [];
			if(moveStep){
				var maxNum = totalHeight-pageHeight;
				for(var i=0;i<=maxNum; i +=moveStep ){
					postions.push(i);
				}
				if(i>maxNum)postions.push(maxNum);
			}else{	
				for(var i=0;i<totalHeight; i +=pageHeight ){
					postions.push(i);
				}
			}
			that.pagePostions = postions;
			that.totalPage = postions.length;
			
			
		},
		
		changePage:function(__num){
			var that = this,
				tmpPage = (that.currPage-1)+__num,
				postions = that.pagePostions,
				contenter = that.contenter,
				len = postions.length;
			
			
			if(tmpPage<0 || tmpPage > len-1)return;
			that.currPage = tmpPage+1;
			
			var dp = -1*postions[tmpPage];
			if(that.scrollType==1)contenter.moveStart(contenter.moveObj.top(),dp);
			else contenter.top(dp);
			
		}
		
	}
	
	//访问历史记录
	plugs.history = function(){
		var that = this;
		that.key = 'SBORY_URL_HISTORY';
		
		var val = $.mem.getVar(that.key,1);
		
		that.history = val===''?[]:val;		
		that.backUrl = '';
		that.addUrl	= '';
		that.initFlag = false;
		
	}

	plugs.history.prototype = {
		init : function(){
			var that = this;
			if(that.initFlag)return;
			that.initFlag = true;
			$.mem.setVar(that.key,'',1);
			that.history = [];
		},
		add : function(url){
			var that = this,
				history = that.history,
				addUrl = 'addUrl',
				backUrl = 'backUrl';
			if(that[addUrl]==''){
				if(that[backUrl]!=''){
					if(history.length>1)history.push(that[backUrl]);
					that[backUrl] = url;
				}
				that[addUrl] = url;
				history.push(url);
				
				$.mem.setVar(that.key,history,1);
			}
		},
		pop : function(){
			var that = this,
				history = that.history,
				popItem = history[0];
				
			if(history.length>0){
				that.addUrl = '';
				popItem = history.pop();
				$.mem.setVar(that.key,history,1);
			}			
			return  popItem || '';
		},
		go : function(__num){
			return this.history[__num];
		},
		back : function(){
			var that = this,
				backUrl = 'backUrl';
			if(that[backUrl]=='')that[backUrl] = that.pop();
			if(that[backUrl]==that.addUrl){
				that[backUrl] = that.pop();
			}
			return that[backUrl];
		},
		head : function(){
			var history = this.history;
			
			return history.length>0?history[0]:'';
			
		},
		tail : function(){
			var history = this.history,
				len = history.length;
			
			return len>0? history[len-1]:'';
			
		}
	};
	
	
})(Sbory);



/*******************   扩展DOM方法区域   ***************/
$.extend({	
		combiString : function(v,w,s){				
			if($.browser=='iPanel' || $.browser == 'iPanelAdvanced'){	
				w = w || that.width();
				s = s || '';
				var that = this;
				return that.e.combiString(v,w,s);
			}
			return v;		
		},	
		divideString : function(v){
			if($.browser=='iPanel' || $.browser == 'iPanelAdvanced'){
				return this.e.divideString(v);
			}
			return [];
		},
		ajax : function(url){
			var that = this,
				ele = that.e,
				p = $.browser == 'iPanel'?'innerText':'innerHTML';
			$.ajax({url:url,success:function(msg){
				ele[p] = msg;
			},error:function(e){
				ele[p] = e;
			}})
		}
});

$.each([
	['text','innerText'],
	['html','innerHTML'],
	['value'],
	['src'],
	['offsetHeight'],
	['offsetWidth'],
	['scrollLeft'],
	['className']
	],function(v){
			var key = v[v.length-1];
			$.extend(v[0],function(val){
				var that = this,
					e = that.e;
				if(typeof(val) == 'undefined'){
					return e[key];
				}else{				
					e[key] = val;
					return that;
				}
			});
		}		
);
$.each(['width','height','left','top'],function(v){			
			$.extend(v,function(val){	
				var flag = typeof(val)!='undefined',
					fn_parseInt = parseInt,
					ret = this.style(v,flag?fn_parseInt(val)+'px':val);
				return flag?ret:fn_parseInt(ret);
		
			});
		}		
);

$.each([['color','color','','#000000'],
		['bgColor','backgroundColor','','transparent'],
		['show','visibility','visible'],
		['hide','visibility','hidden'],
		['webkit','webkitTransitionDuration','','']
		],function(v){
			var key = v[1];

			$.extend(v[0],function(val){
				return this.style(key, val == ''?(v[3] || v[2]):(v[2] || val));
			});
			
		}
);
$.media ={
	x:0,
	y:0,
	w:0,
	h:0,
	player:null,
	status : 'init',
	instanceID : -1,
	open:function(url,type,x,y,w,h){
		this._setXYWH(x,y,w,h);
		switch($.browser){
			case 'iPanel':
				if(type){
					media.AV.open(url,type);
				}else{
					if(url.toLowerCase().indexOf("http://")==0){
						media.AV.open(url,'HTTP');
					}else{
						media.AV.open(url,'VOD');//2014-10-27从HTTP修改为VOD，因为陕广的VOD视频都是RTSP路径
					}
				}
				break;
			default:
				$.media.play(url,x,y,w,h);
				break;
		}
		
	},
	play:function(url,x,y,w,h){
		var that = this;
		switch($.browser){
			case 'iPanel':
				media.AV.play();
				that.status = 'play';
				break;
			case 'iPanelAdvanced':
				if(that.player ==null)that.player = new MediaPlayer();
				var _player = that.player;
				
				that.instanceID = _player.createPlayerInstance("video",2);
				
				_player.bindPlayerInstance(that.instanceID);
				_player.position = [that.w >0 && that.h >0?0:1,that.x,that.y,that.w,that.h].join(',');//"0,300,300,350,250";//
				_player.source = url;
				_player.refresh();
				_player.play();
				
				$.media._success();
				break;
			case 'Coship':
				if(that.player==null)that.player = new MediaPlayer();
				var _player = that.player;
					_player.setSingleMedia(url);
					_player.playFromStart();
				$.media._success();
				break;
			case 'Hisu':
				DVB.stop();
				MediaPlayerIF.stopAllPlayer();
				MediaPlayerIF.getNewPlayer();
				
				MediaPlayerIF.signal.connect(that._hisuEvent);				
				MediaPlayerIF.enableEvent();
				var flag = MediaPlayerIF.play(url, that.x, that.y, that.w, that.h);
				if(flag){
					$.media._success();
				}
				else{
					$.media._disconnectEvent();
					$.media._failed();
				}
				break;
			case 'Sunniwell':
				if(that.player==null)that.player = new MediaPlayer();
				var _player = that.player,
					mediaStr='[{mediaUrl:"'+url+'"}]';
				
				_player.setSingleMedia(mediaStr);
				_player.playFromStart();
				that.refreash();
				$.media._success();
				break;
			case 'Chrome':
				
				var	embedObj = $('chrome_browser_video_div'),
					videoStr = '<video id="chrome_browser_media" autoplay=""  autobuffer="true" oncanplay="$.media._success()" width="'+that.w+'" height="'+that.h+'" name="media" onended="$.media._over();"><source src="'+url+'" ></video>';
				if(embedObj.e == null){
					var embedDiv = document.createElement('div');
					embedDiv.setAttribute('id','chrome_browser_video_div');
					embedDiv.setAttribute('style','position:absolute;left:'+that.x+'px;top:'+that.y+'px;z-index:-100;');
					$(embedDiv).html(videoStr);
					document.getElementsByTagName('body')[0].appendChild(embedDiv);
				}else{
					embedObj.html(videoStr);
				}
				that.player =  $('chrome_browser_media').e;
				break;
			case 'Webdroid':
			case 'Chnavs':
				break;
			case "Android":
				AndroidMediaPlayer.play(url,that.x, that.y, that.w, that.h);
				that.status = 'play';
				break;
			case "OTTSW":
				SWMediaPlayer.play(url,that.x, that.y, that.w, that.h);
				that.status = 'play';
				break;
			case 'RocME':
				if(that.player ==null)that.player = new MediaPlayer();
				var _player = that.player;
				
				if(that.instanceID < 0 ){
					var tmpId =  ($.mem.getVar('RocME_Media_instanceId') || -1)-0;
					if(tmpId < 0){
						that.instanceID = _player.createPlayerInstance("video",2);
						$.mem.setVar('RocME_Media_instanceId',that.instanceID);
					}
					that.instanceID = tmpId;
				}
				
				_player.bindPlayerInstance(that.instanceID);
				_player.position = [that.w >0 && that.h >0?0:1,that.x,that.y,that.w,that.h].join(',');//"0,300,300,350,250";//
				_player.source = url;				
				_player.play();
				
				$.media._success();
				break;
		}
	},
	pause:function(){
		var that = this;
		switch($.browser){
			case 'iPanel':
				media.AV.pause();
				break;
			case 'Coship':
			case 'Sunniwell':	
			case 'Chrome':
				that.player.pause();
				break;
			case 'Webdroid':
				break;
			case 'Chnavs':
				break;
			case 'Hisu':
				MediaPlayerIF.pause();
				break;
			case 'OTTSW':
				SWMediaPlayer.pause();
				break;
			case "Android":
				AndroidMediaPlayer.pause();
				break;
			case 'iPanelAdvanced':
			case 'RocME':
				that.player.pause(1);
				break;
		}
		that.status = 'pause';
	},
	resume:function(){
		var that = this;
		switch($.browser){
			case 'iPanel':
				media.AV.play();
				break;
			case 'Coship':
			case 'Sunniwell':	 
				that.player.resume();
				break;
			case 'Hisu':
				MediaPlayerIF.resume();
				break;
			case 'Chrome':
				that.player.play();
				break;
			case 'Webdroid':
				break;
			case 'Chnavs':
				break;
			case "OTTSW":
				SWMediaPlayer.resume();
				break;
			case "Android":
				AndroidMediaPlayer.resume();
				break;
			case 'iPanelAdvanced':
			case 'RocME':
				var _player = that.player;
				_player.pace = 1;
				_player.play();
				break;
		}
		that.status = 'play';
	},
	
	stop:function(){
		var that = this;
		switch($.browser){
			case 'iPanel':
				media.AV.stop(0);
				media.AV.close();
				break;
			case 'Hisu':
				MediaPlayerIF.signal.disconnect($.media._hisuEvent);
				MediaPlayerIF.stopAllPlayer();
				break;
			case 'Coship':
				that.player.stop();
				break;
			case 'Sunniwell':
				var _player = that.player;
				_player.stop();
				_player.clearAllMedia();
				break;
			case 'OTTSW':
				SWMediaPlayer.stop();
				break;
			case "Android":
				AndroidMediaPlayer.stop();
				break;

			case 'Chrome':
				var	embedObj = $('chrome_browser_video_div');
				if(embedObj.e!=null){
					embedObj.html('');
				}
				break;
			case 'Webdroid':
				break;
			case 'Chnavs':
				break;
			case 'RocME':
				var _player = that.player;
				_player.unBindPlayerInstance();
				_player.releasePlayerInstance();
				break;
			case 'iPanelAdvanced':
				var _player = that.player;
				_player.pause(0);
				_player.releasePlayerInstance();
				_player.unBindPlayerInstance();
				break;
		}
		that.status = 'stop';
	},
	setPosition:function(x,y,w,h){
		switch($.browser){
			case 'iPanel':
				media.video.setPosition(x,y,w,h);
				break;			
			/*case 'Sunniwell':
				var mp = new MediaPlayer();
				mp.setVideoDisplayArea(x,y,w,h);
				break;
			case 'OTTSW':
				SWMediaPlayer.setLocation(x,y,w,h);
				break;*/
			default:
				this._setXYWH(x,y,w,h);
				break;
		}
		this.refreash();
	},
	_setXYWH:function(x,y,w,h){
		var that = this;
		that.x = x || that.x;
		that.y = y || that.y;
		that.w = w || that.w;
		that.h = h || that.h;
	},

	fullScreen:function(){
		var that = this;
		switch($.browser){
			case 'iPanel':
				media.video.fullScreen();
				break;
			case 'iPanelAdvanced':
			case 'Sunniwell':
			case 'RocME':
				that.x = that.y = that.w = that.h = 0;
				break;
			default:
				that.x = that.y = 0;
				that.w = 1280;
				that.h = 720;
				break;
		}
		this.refreash();
	},

	refreash : function(){
		var that = this,
			_player = that.player;
		switch($.browser){
			case 'Sunniwell':
				if(!_player)break;
				if(that.w >0 && that.h >0){
					_player.setVideoDisplayArea(that.x, that.y, that.w, that.h);
					_player.setVideoDisplayMode(0);
				}else {
					_player.setVideoDisplayMode(1);
				}
				_player.refreshVideoDisplay();
				break;
			case 'Chrome':
				if(!_player)break;
				$('chrome_browser_video_div').left(that.x).top(that.y);
				_player.width=that.w;
				_player.height=that.h;
				break;
			case 'Android':
				AndroidMediaPlayer.setLocation(that.x, that.y, that.w, that.h);
				break;
		}	
		
	},

	getDuration :function(){
		var that = this;
		switch($.browser){			
			case "OTTSW":
				return SWMediaPlayer.getDuration(); //秒
				break;
			case "Android":
				return AndroidMediaPlayer.getDuration(); //秒
				break;
			case 'Hisu':
				return Math.round(MediaPlayerIF.getDuration()/1000);//毫秒
				break;
			case 'Sunniwell':
			case 'RocME':
			case 'iPanelAdvanced':
				return that.player.getMediaDuration();
				break;
			case 'Chrome':
				return Math.round(that.player.duration || 0);
				break;
			case 'Webdroid':
				return 0;
				break;
			case 'Chnavs':
				return 0;
				break;
		}
	},
	getCurrentPlayTime:function(){
		var that = this;
		switch($.browser){			
			case "OTTSW":
				return SWMediaPlayer.getCurrentPosition(); //秒
				break;
			case "Android":
				return AndroidMediaPlayer.getCurrentPosition(); //秒
				break;
			case 'Hisu':
				return $.media._hisuPlayPos;
				break;
			case 'Sunniwell':
				return that.player.getCurrentPlayTime();
				break;
			case 'RocME':
			case 'iPanelAdvanced':
				return that.player.currentPoint;
				break;
			case 'Chrome':
				return Math.round(that.player.currentTime || 0);
				break;
			case 'Webdroid':	
				return 0;
				break;
			case 'Chnavs':
				return 0;
				break;
		}	
	},
	seek : function(param){
		var that = this,
			oStatus = that.status;
		if(oStatus == 'seek')return;
		that.status = 'seek';
		switch($.browser){			
			case "OTTSW":
				SWMediaPlayer.seek(param); //秒
				break;
			case "Android":
				AndroidMediaPlayer.seek(param); //秒
				break;
			case 'Sunniwell':
				that.player.playByTime(1,param);
				break;
			case 'Hisu':
				MediaPlayerIF.seek(param);	
				break;
			case 'RocME':
			case 'iPanelAdvanced':
				var _player = this.player;
				_player.point = param;
				_player.refresh();
				break;
			case 'Webdroid':
			case 'Chnavs':
			case 'Chrome':
				var _player = that.player;
				if(!_player.seeking)that.player.currentTime = param;
				break;
		}
		that.status = oStatus;
	},
	
	volumeNum : -1,
	getVolume : function(real){
		var that = this,
			num = that.volumeNum;
		real = real===false?false:true;
		if(num>-1 && !real){
			return num;
		}
		switch($.browser){
			case 'Chrome':
				num = that.player.volume*100;
				break;
			case 'Android':
				num = AndroidMediaPlayer.getVolume();
				break;
			case 'iPanel':
				num = media.sound.value;
				break;
			case 'iPanelAdvanced':
				break;
		}
		that.volumeNum = num;
		return num;
	},
	setVolume : function(__num){
		if(__num<0 || __num>100)return;
		switch($.browser){
			case 'Chrome':
				this.player.volume = (__num/100).toFixed(2);
				break;
			case 'Android':
				AndroidMediaPlayer.setVolume(__num);
				break;
			case 'iPanel':
				media.sound.value = __num;
				break;
			case 'iPanelAdvanced':
				break;
		}
	},
	adjustVolume : function(__num,real){
		var that = this;
		if(that.volumeNum<0){
			that.volumeNum = that.getVolume(real);
		}
		var tmpVolume = that.volumeNum+__num;
		if(tmpVolume<0 || tmpVolume>100){
			return;
		}
		that.volumeNum = tmpVolume;
		that.setVolume(tmpVolume);
		return tmpVolume;
	},

	isMuted : function(){
		switch($.browser){
			case 'Chrome':
				return this.player.muted;
				break;
			case 'Android':
				break;
			case 'iPanel':
				break;
			case 'iPanelAdvanced':
				break;
		}
		return false;
	},
	muted : function(){
		var that = this;
		switch($.browser){
			case 'Chrome':
				this.player.muted = that.isMuted()?false:true;
				break;
			case 'Android':
				break;
			case 'iPanel':
				break;
			case 'iPanelAdvanced':
				break;
		}
		return that.isMuted();
	},

	_hisuPlayPos : 0,
	
	_hisuEvent:function(index, cmd, param){
		var PLAYERMSG_STATECNG = 0,
			PLAYERMSG_ERR = 1,
			PLAYERMSG_BUFFER = 2,
			PLAYERMSG_POS = 3,
			PLAYERMSG_REGION = 4,
			PLAYERSTATE_IDLE = 0,
			PLAYERSTATE_BUFFER = 1,
			PLAYERSTATE_PAUSE = 2,
			PLAYERSTATE_PLAY = 3,
			PLAYERSTATE_STOP = 4,
			PLAYERSTATE_SEEK = 5,
			PLAYERSTATE_WAITSTOP = 6;
			switch(cmd){
				case PLAYERMSG_STATECNG:
					if(param == PLAYERSTATE_PAUSE){
					}
					else if(param == PLAYERSTATE_PLAY){
					}
					else if(param == PLAYERSTATE_STOP){
						$.media.stop();
						$.media._over();
						
					}
					break;
				case PLAYERMSG_POS:
					$.media._hisuPlayPos = param;
					break;
			}
	},
	_connectEvent : function(){
		if($.browser == 'Hisu')MediaPlayerIF.signal.connect($.media._hisuEvent);
	},

	_disconnectEvent : function(){
		if($.browser == 'Hisu')MediaPlayerIF.signal.disconnect($.media._hisuEvent);
	},
	_success:function(){
		var status = this.status;
		if(status == 'init'){
			var tmpObj = {key:'VOD_PLAY_SUCCESS',code:5205,type:2,p2:-1};
			$.plugs.keyProcess(tmpObj.key,tmpObj);
			this.status = 'play';
		}
	},
	_failed:function(){
		var tmpObj = {key:'VOD_PLAY_FAILED',code:5206,type:2,p2:-1};
		$.plugs.keyProcess(tmpObj.key,tmpObj);
	},
	_over:function(){
		setTimeout(function(){
			var tmpObj = {key:'VOD_PROGRAM_END',code:5210,type:2,p2:-1};
			$.plugs.keyProcess(tmpObj.key,tmpObj);
		},500);
	}
};

$.BgMusic = {
	playType : -1,
	operator : "",
	url : "",
	fun : null,
	playMusic : function (type, url, operator, time, fun) {
		var that = this,
			browser = $.browser;
		that.playType = type;
		that.operator = operator;
		that.url = url;
		switch(type){
			case 0:
			case "0":
				if(browser == 'iPanel' || browser == 'iPanelAdvanced'){
					var empty = $.empty;
					if(url != ""){
						if(!empty(operator) && operator.indexOf("陕西") > -1){
							media.AV.open(url,"HTTP");
						}
						else if(new Boolean(MP3)==true){
							if(new Boolean(MP3.setProperty)==true)MP3.setProperty("beginData","50");
							if(!empty(time)) MP3.open(url, time);
							else MP3.open(url, 100);
						}
					}
				}
				else $.media.open(url);
				break;
			case 1:
			case "1":
				if(browser == 'iPanel' || browser == 'iPanelAdvanced'){
					if(url != ""){
						var url_str = url.split(":"),
							frequency = parseInt(url_str[0]),
							serviceId = parseInt(url_str[1]);
						DVB.playAV(frequency, serviceId);
					}
				}
				else if(browser == 'Coship'){
					if(url != ""){
						var url_str = url.split(":"),
							frequency = parseInt(url_str[0]),
							serviceId = parseInt(url_str[1]),
							mp = new MediaPlayer(),
							play_url = "deliver://"+frequency+".6875.64."+serviceId;//这里的frequency的单位是HZ，如果通过同洲接口获取到的service对象的frequency属性值单位为kHZ，所以要转为HZ需要乘以1000
							
						mp.setSingleMedia(play_url);
						mp.playFromStart();
					}
				}
				break;
			default:
				break;
		}
	},

	doPlayMusic : function (operator){
		if($.browser == 'iPanel' || $.browser == 'iPanelAdvanced'){
			if(!$.empty(operator)  && operator.indexOf("陕西") > -1) media.AV.play();
		}
	},

	pauseMusic : function (operator){
		var operator = this.operator,
			browser = $.browser;
		if(browser == 'iPanel' || browser == 'iPanelAdvanced'){
			if(!$.empty(operator) && operator.indexOf("陕西") > -1){
				media.AV.pause();
			}
			else if(new Boolean(MP3)==true){
				 MP3.pause();
			}
		}else $.media.pause();
	},
	
	resumeMusic : function (){
		var operator = this.operator,
			browser = $.browser;
		if(browser == 'iPanel' || browser == 'iPanelAdvanced'){
			if(!$.empty(operator)  && operator.indexOf("陕西") > -1){
				media.AV.play();
			}
			else if(new Boolean(MP3)==true){
				MP3.resume();
			}
		}
		else $.media.resume();
	},
	
	closeMusic : function(){
		var that = this,
			browser = $.browser,
			operator = that.operator;
			
		switch(that.playType){
			case 0:
			case "0":
				if(browser == 'iPanel' || browser == 'iPanelAdvanced'){
					if(!$.empty(operator) && operator.indexOf("陕西") > -1){
						media.AV.stop();
						media.AV.close();
					}
					else if(new Boolean(MP3)==true){
						MP3.stop();
						MP3.close();
					}
				}else $.media.stop();
				break;
			case 1:
			case "1":
				if(browser == 'iPanel' || browser == 'iPanelAdvanced') DVB.stopAV();
				else if(browser == 'Coship'){
					var mp = new MediaPlayer();
					mp.stop();
				}
				break;
			default:
				if(browser == 'iPanel' || browser == 'iPanelAdvanced'){
					if(new Boolean(MP3)==true){
						MP3.stop();
						MP3.close();
					}
					else{
						media.AV.stop();
						media.AV.close();
					}
					DVB.stopAV();
				}else $.media.stop();
				break;
		}
	},
	connectEvent : function(){
		$.media._connectEvent();
	},

	disconnectEvent : function(){
		$.media._disconnectEvent();
	}
};

$.CA = {
	getID : function(success){
		return $.STB.getCaId(success);
	}
};

$.STB = {
	getCaId : function(success){
		var caId = $.mem.getVar('STB_CA_CARD_ID');
		if(caId){
			if(typeof success == 'function')success(caId);
			return caId;
		}
		caId = '90000000000000009';
		switch($.browser){
			case 'iPanel':
			case 'iPanelAdvanced':
				caId = CA.card.serialNumber;
				break;
			case "Coship":
				caId = CA.serialNumber;
				break;
			case "Chrome":
				caId =  "90000000000000001";
                break;
            case 'Chnavs':
                caId = tplat.dtv.extensions.system.serial;
				break;
			case "Hisu":
				//caId = DVB.getCAInfo().cardNum;
				caId = System.getClientSN();
				break;
			case "Sunniwell":
				caId = iPanel.ioctlRead("ntvuseraccount");
				break;
			case "OTTSW":
				caId = SWSystem.getConfig("serial");
				break;
			case "Webdroid":
			case 'WeiQiao':
				CAManager.getAllCardInfos(function(allCardInfos){
					var len = allCardInfos.length,i=0,csn;
					for(;i<len;i++){
						csn = allCardInfos[i].cardSerialNumber;
						if(csn){
							caId = csn;
							if(typeof success == 'function')success(caId);
							$.mem.setVar('STB_CA_CARD_ID',caId);
							break;
						}
					}
					
				},function(err){
					console.log($.browser+" === getAllCardInfos error === " + err);
				});
				return;
		}
		if(typeof success == 'function')success(caId);
		$.mem.setVar('STB_CA_CARD_ID',caId)
		return caId;
	},
	getMAC : function(success){
		var macAddress = $.mem.getVar('STB_NETWORK_MAC_ADDRESS');
		if(macAddress){
			if(typeof success == 'function')success(macAddress);
			return macAddress;
		}
		macAddress = "00-00-00-00-00-2D";
		switch($.browser){
			case 'iPanel':
			case 'iPanelAdvanced':
				macAddress = network.ethernets[0].MACAddress;
				break;
			case "Coship":
				var ethernet = Network.ethernets[0];
				macAddress = ethernet.MACAddress;
				break;
			case "Chrome":
				macAddress =  "ff-ff-ff-ff-ff-ff";
                break;
            case 'Chnavs':
                var mac = tplat.dtv.setting.net.mac;
	            if(!$.empty(mac)){
					var a = mac.toString().split(':'),
						len = a.length,
						ret = [];
				    for(var i = 0; i<len;i++){
				    	ret.push(a[i]);
						if(i!=len-1){
							ret.push('-');
						}
					}
	                macAddress= ret.join("");
			    }	         
				break;
			case "Hisu":
				var mac_addr = "";
				var stb_id = System.getStbID();
				if(!$.empty(stb_id)){
					mac_addr = stb_id.substr(14);
					macAddress = $.STB.format(mac_addr,2,'-');
				}
				break;
			case "Sunniwell":
				macAddress = iPanel.ioctlRead("ntvuseraccount");
				break;
			case 'GXCATV'://广西那边在页面上面暂时还没有获取MAC的接口，先用获取机顶盒序列号来代替
				macAddress = guangxi.getStbNum();
				break;

			case 'Webdroid':
			case "WeiQiao":
				if(window.Broadband && window.Broadband.getAllEthernets){
					window.Broadband.getAllEthernets(function(objs) {
						var mac;
						for (var i = 0; i < objs.length; i++) {
							mac = objs[i].MACAddress;
							if(mac){
								macAddress = mac;
								success(macAddress);
								$.mem.setVar('STB_NETWORK_MAC_ADDRESS',macAddress);
								break;
							} 
						}
					});
				}
				return;
			case "Android":
				macAddress = System.getMacAddress();
				if(macAddress && macAddress.indexOf('-')<0){
					macAddress = $.STB.format(macAddress,2,'-');
				}
				break;

		}
		if(typeof success == 'function')success(macAddress);
		$.mem.setVar('STB_NETWORK_MAC_ADDRESS',macAddress);
		return macAddress;
	},	
	format : function(str,num,_s){
		_s = _s || '.';
		var a = str.toString().split(''),
			len = a.length,
			ret = [];
		for(var i = 0,j=1; i<len;i++,j++){
			ret.push(a[i]);
			if(j%num==0 && i!=len-1){
				ret.push(_s);
			}
		}
		return ret.join('');
	}
};

$.checkCardId = function(socialNo){

	  if (socialNo.length != 15 && socialNo.length != 18)
	  {
	    return ("输入身份证号码格式不正确!");
	  }
		
	 var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}; 
	   
	   if(area[parseInt(socialNo.substr(0,2))]==null) {
	    	return ("身份证号码不正确(地区非法)!");
	   } 
	    	
	  if (socialNo.length == 15)
	  {
	     pattern= /^\d{15}$/;
	     if (pattern.exec(socialNo)==null){
			return ("15位身份证号码必须为数字！");
	    }
		var birth = parseInt("19" + socialNo.substr(6,2));
		var month = socialNo.substr(8,2);
		var day = parseInt(socialNo.substr(10,2));
		switch(month) {
			case '01':
			case '03':
			case '05':
			case '07':
			case '08':
			case '10':
			case '12':
				if(day>31) {
					return '输入身份证号码不格式正确!';
				}
				break;
			case '04':
			case '06':
			case '09':
			case '11':
				if(day>30) {
					return '输入身份证号码不格式正确!';
				}
				break;
			case '02':
				if((birth % 4 == 0 && birth % 100 != 0) || birth % 400 == 0) {
					if(day>29) {
						return '输入身份证号码不格式正确!';
					}
				} else {
					if(day>28) {
						return '输入身份证号码不格式正确!';
					}
				}
				break;
			default:
				return '输入身份证号码不格式正确!';
		}
		var nowYear = new Date().getYear();
		if(nowYear - parseInt(birth)<15 || nowYear - parseInt(birth)>100) {
			return '输入身份证号码不格式正确!';
		}
	    return '';
	  }
	  
	  var Wi = new Array(
	            7,9,10,5,8,4,2,1,6,
	            3,7,9,10,5,8,4,2,1
	            );
	  var   lSum        = 0;
	  var   nNum        = 0;
	  var   nCheckSum   = 0;
	  
	    for (i = 0; i < 17; ++i)
	    {
	        

	        if ( socialNo.charAt(i) < '0' || socialNo.charAt(i) > '9' )
	        {
	            return ("输入身份证号码格式不正确!");
	        }
	        else
	        {
	            nNum = socialNo.charAt(i) - '0';
	        }
	         lSum += nNum * Wi[i];
	    }

	  
	    if( socialNo.charAt(17) == 'X' || socialNo.charAt(17) == 'x')
	    {
	        lSum += 10*Wi[17];
	    }
	    else if ( socialNo.charAt(17) < '0' || socialNo.charAt(17) > '9' )
	    {
	        return ("输入身份证号码格式不正确!");
	    }
	    else
	    {
	        lSum += ( socialNo.charAt(17) - '0' ) * Wi[17];
	    }
	    
	    if ( (lSum % 11) == 1 )
	    {
	        return '';
	    }
	    else
	    {
	        return ("输入身份证号码格式不正确!");
	    }
		
};

$.plugs.dialog ={
		lastFocusArea : 9999,
		focusArea : 9999,
		btnPos : 0,
		keyProcess : function(keyObj){ return 1;}
};

/****************************应用代码******************************/

//配置信息
var INTERFACE_SERVER="cdui.hunancatv.com",
	OPEN_USER = "hotel",
	OPEN_PASSWD = "123456",
	
	HOTEL_AUTH_INFO = null,
	HEART_TIME_INTERVAL = 5,
	HOTEL_NOTICE_INTERVAL = 2,
	
	SMART_AD_SERVER="cdui.hunancatv.com:8086";

function initRequestUrl(api,params){
	HOTEL_AUTH_INFO	= HOTEL_AUTH_INFO || $.mem.getVar('HISU_SMART_HOTEL_ROOM_INFO');//房间信息，开机认证时获得

	params = params || {};
	params.requestUser = OPEN_USER;
	params.requestPassword = OPEN_PASSWD;
	
	params.roomName = params.roomName || HOTEL_AUTH_INFO.roomName;
		
	var p = [];
	for(var k in params){
		p.push(k+"="+params[k]);		
	}
	
	
	return api = 'http://' + (HOTEL_AUTH_INFO.miniServerIP || INTERFACE_SERVER) + api + "?" + p.join("&");
}

function initSmartAdUrl(api,params){
	params = params || {};
	params.requestUser = OPEN_USER;
	params.requestPassword = OPEN_PASSWD;

	var p = [];
	for(var k in params){
		p.push(k+"="+params[k]);		
	}

	return api = 'http://' + SMART_AD_SERVER + api + "?" + p.join("&");
}

function urlCmdMap(cmdStr){
	/*cmdStr的样例，不包括【】
	【cmd:exittotv:hunan】
	或 
	【cmd:android:[{"package":"","activity":"","arguments":{"mode":1},"download":"http://ip:port/xx.apk"}]:hunan】

	第一个分号之前的是固定的CMD标识
	第一个分号之后的是命令字符串
	第三个是命令参数（可选用，JSON格式字符串）
	最后一个分号之后的是运营商或应用场景标识
	*/
	var cmd = cmdStr.toLowerCase().split(":")[1];//执行的命令
	var operator = cmdStr.substring(cmdStr.lastIndexOf(':')+1);//对应的运营商或应用场景标识
	switch(cmd){
		case 'exittotv'://基本频道
			if($.browser == 'iPanel'){//湖南广电的进入直播写法
				if(operator == 'hunan'){
					var E = iPanel.eventFrame;
					var user = users.currentUser;
					if(E.AllChannelLength == 0){
						iPanel.overlayFrame.location.href = "ui://overlay_confirm.htm?search";
						return;
					}else{
						E.indexGoPlay = 1;
						var currentChannel = user.getOffChannel(1);
						var flag = true;
						if(!E.isHd(currentChannel.userChannel)){            //关机频道不是高清频道
							flag = E.isLogic(currentChannel.userChannel);   
						}
							
						if(!flag){                                          //也不是付费频道
								currentChannel.open();
						}else{                                                                                                        
							var currChannel = E.chanel_list[0] ;
							currChannel.open();
						}
					}
										
				}else if(operator=='shannxi'){
					window.location.href="ui://index.htm?auto";
				}
			}
			else if($.browser == 'Hisu'){
				DVB.init();
				var c_index = 0;
				var c_c_num = System.getConfig('curchan');
				if (c_c_num != null && c_c_num.length > 0) c_index = parseInt(c_c_num);
				else c_index = 1;

				DVBPlayer.setStartChannelIndex(c_index);
				if( DVBPlayer.getTVChanNum() > 0){
					var tvChannel = DVBPlayer.getChannelInfo();

					var _channel = DVBPlayer.getTVChannelDetailByID(tvChannel.serviceId,tvChannel.tsId,tvChannel.networkId);
					
					DVBPlayer.setTVVoutRect(0,0,1280,720);
					if(_channel && _channel.name){
						//DVBPlayer.playTVChannel(_channel.serviceId);
						window.location.href = 'file:///ui/web/dvb/dvbplay.html?chan='+_channel.channelNo;
					}
					System.setConfig('curchan', _channel.channelNo);
				}
			}
			break;
		case 'exittohdtv'://进入高清频道播放
			if($.browser == 'iPanel'){//湖南广电的进入直播写法
				if(operator == 'hunan'){
					//根据现场的反馈需要打开跟本地UI里面的T字型首页直播下面的高清频道看的视频一致
					var E = iPanel.eventFrame;
					E.indexGoPlay = 1;
					var channlist = E.hdList;
					if(channlist.length>0){
						channlist[0].open();
					}else{
						iPanel.overlayFrame.location.href = "ui://overlay_confirm.htm?search";
					}
				}
			}
			break;
		case 'hisuhdtv':
			if($.browser=='Hisu'){
				    var url="file:///ui/web/dvb/dvbplay.html";
					Server.loadLocalPage(url);  //数字电视跳转 zzj
					System.setWebEnv("backurl",url);
			}
			break;
		case 'hisulocalplay':
			if($.browser=='Hisu'){
				    var url="file:///ui/web/localmedia/filebrowse.html";
					Server.loadLocalPage(url);  //广电互动跳转 zzj
					System.setWebEnv("backurl",url);
			}
			break;
		case 'android':
			if(typeof System =='undefined' || typeof JSON == 'undefined'){
				alert('非法环境！');
				break;
			}
			var spos = cmdStr.indexOf('android:')+8,
				epos = cmdStr.lastIndexOf(':');
				cmdPramArr = null;
			try{
				cmdPramArr = eval($.url.decode(cmdStr.substring(spos,epos)));
			}catch(e){
				System.toast('命令参数错误，请检查！');
				break;
			}
			if(cmdPramArr && cmdPramArr.length>0){
				for(var i=0,len=cmdPramArr.length;i<len;i++){
					var cp = cmdPramArr[i];
					if(cp.uninstall && System.uninstallApp){
						System.uninstallApp(cp.download);
					}
					var ret = System.openApp(cp.package,cp.activity||'',cp.arguments?JSON.stringify(cp.arguments):'');
					if(ret==0)break;
					if(ret<0 && cp.download){
						System.downloadApk(cp.download);
						break;
					}
				}
				//$.media.stop();
			}
			break;	
		default:
			break;
	}
}

setTimeout(function(){
	hotelUserBehavior();
	hotelHeartTime();
	setTimeout(hotelMsgNotice,1000);
	
},1000);
	
//心跳
function hotelHeartTime(){

	HOTEL_AUTH_INFO	= HOTEL_AUTH_INFO || $.mem.getVar('HISU_SMART_HOTEL_ROOM_INFO');//房间信息，开机认证时获得
	
	var intervalTime = HEART_TIME_INTERVAL*60*1000;

	if(!$.empty(HOTEL_AUTH_INFO)){
		var now = ($.date.currentDate || new Date()).getTime();
		var datetime	= HOTEL_AUTH_INFO.datetime;
		
		
		if(!$.empty(datetime)){
			var mSec = now-datetime,
				min = Math.floor(mSec/intervalTime),
				leftMillSec	= mSec%intervalTime;
			//alert(HOTEL_AUTH_INFO.stbMac);
			if(min>0){
				HOTEL_AUTH_INFO.datetime = now-leftMillSec;
				$.mem.setVar('HISU_SMART_HOTEL_ROOM_INFO',HOTEL_AUTH_INFO);
				
				var heartUrl = initRequestUrl('/SmartHotelInterface/api/smartHotel/heartTime',{stbMac:HOTEL_AUTH_INFO.stbMac});
				//alert(heartUrl);
				(new Image()).src = heartUrl;

				//heartUrl += min; 
			}
			
			if(leftMillSec>1000){
				setTimeout(hotelHeartTime,intervalTime-leftMillSec);//arguments.callee
				return;
			}
		}else{
			HOTEL_AUTH_INFO.datetime = now;
			$.mem.setVar('HISU_SMART_HOTEL_ROOM_INFO',HOTEL_AUTH_INFO);
		}
			
	}
	setTimeout(hotelHeartTime,intervalTime);
}

//行为收集
function hotelUserBehavior(){
	var pageUrl = window.location.href.split('?')[0];
	var isStatic = true;
	if(pageUrl.indexOf('/static/')<0 || pageUrl.indexOf('/lang_')<0 ){
		isStatic = false;
		if(pageUrl.indexOf('/worldTime.htm')<0 && pageUrl.indexOf('/hotelMessage.htm')<0){
			return;
		}
	}
	var params = {};
	if(isStatic){
		var endPos = pageUrl.lastIndexOf('/',pageUrl.lastIndexOf('/')-1);
		var startPos = 	pageUrl.lastIndexOf('/',endPos-1);	
		params.functionCode = pageUrl.substring(startPos+1,endPos);
	}
	HOTEL_AUTH_INFO = HOTEL_AUTH_INFO || $.mem.getVar('HISU_SMART_HOTEL_ROOM_INFO');//房间信息，开机认证时获得
	if($.empty(HOTEL_AUTH_INFO) && params.functionCode!='welcome')return;
	
	
	var pageFileName = $.url.getFilename();	
	var pageDesc = '';
	switch(params.functionCode){
		case 'welcome'://欢迎页
			pageDesc = '欢迎页';
			var _hotelRoomInfo = $.url.query('hotelRoomInfo');
			//console.info(_hotelRoomInfo);
			if(_hotelRoomInfo){
				HOTEL_AUTH_INFO = $.util.unserialize($.url.decode(_hotelRoomInfo));
				$.mem.setVar('HISU_SMART_HOTEL_ROOM_INFO',HOTEL_AUTH_INFO);
			}
			break;
		case 'portal':
			pageDesc = '首页';
			break;
		case 'switchLanguage':
			pageDesc = '语言切换';
			break;
		case 'keyInfo':
			pageDesc = '操作说明';
			break;		
		case 'clearService':
			pageDesc = '清洁服务';
			break;
		case 'serviceGuide':
			pageDesc = '服务指南';
			break;
		case 'hotelIntroduce':
			pageDesc = '酒店介绍';
			break;
		case 'meetingFacilities':
			pageDesc = '会议设施';
			break;
		case 'diningFacilities':
			pageDesc = '餐饮设施';
			break;
		case 'healthCenter':
			pageDesc = '康体中心';
			break;
		case 'safeInfo':
			pageDesc = '安防信息';
			break;
		case 'mealService':
			pageDesc = '送餐服务';
			break;
		case 'hotelMarket':
			pageDesc = '便利超市';
			break;
		case 'roomFacilities':
			if(pageFileName.indexOf('index')>-1){
				pageDesc = '客房设施列表';
			}else if(pageFileName.indexOf('detail')>-1){
				pageDesc = '客房设施详情';
			}
			break;
		case 'vod':
			if(pageFileName.indexOf('index')>-1){
				pageDesc = 'VOD点播';
			}else if(pageFileName.indexOf('topic_list')>-1){
				pageDesc = 'VOD专题';
			}else if(pageFileName.indexOf('detail_single')>-1){
				pageDesc = 'VOD影片详情';
			}else if(pageFileName.indexOf('detail_teleplay')>-1){
				pageDesc = 'VOD电视剧详情';
			}
			break;
		default:
			if(!isStatic){
				if(pageUrl.indexOf('/worldTime.htm')<0){
					params.functionCode = 'worldTime';
					pageDesc = "世界时间";
					break;
				}
				if(pageUrl.indexOf('/onlineExam.htm')<0){
					params.functionCode = 'onlineExam';
					pageDesc = "在线考试";
					break;
				}
				if(pageUrl.indexOf('/hotelMessage.htm')<0){
					params.functionCode = 'hotelMsg';
					pageDesc = "消息通知";
					break;
				}
			}
			pageDesc = pageFileName;
			break;
	}

	params.hotelId = HOTEL_AUTH_INFO.hotelId;
	params.roomId = HOTEL_AUTH_INFO.roomId;
	params.page = pageDesc;
	var behaviorUrl = initRequestUrl('/SmartHotelInterface/api/smartHotel/behaviorcollect',params);
		
	(new Image()).src = behaviorUrl;

}

//消息通知
function hotelMsgNotice(){
	var pageUrl = window.location.href.split('?')[0];
	
	if(pageUrl.indexOf('/static/')<0 || pageUrl.indexOf('/lang_')<0 || pageUrl.indexOf('/welcome/')>-1 || pageUrl.indexOf('/hotelMessage')>-1)return;
	HOTEL_AUTH_INFO	= HOTEL_AUTH_INFO || $.mem.getVar('HISU_SMART_HOTEL_ROOM_INFO');//房间信息，开机认证时获得
	if($.empty(HOTEL_AUTH_INFO))return;
	var params = {
		hotelId : HOTEL_AUTH_INFO.hotelId,
		roomId : HOTEL_AUTH_INFO.roomId,
		num : 1,
		readStatus : 0
	};
	var msgUrl = initRequestUrl('/SmartHotelInterface/api/smartHotel/roomMessages',params);
	$.get(msgUrl,hotelMsgNoticeCallback);
	
}

//消息通知回调函数
function hotelMsgNoticeCallback(__text){
	var pageUrl = window.location.href.split('?')[0];
	var tmp = $.eval(__text);
	if(!$.empty(tmp) && tmp.dataList && tmp.dataList.length>0){
		var lang		= $.mem.getVar('HISU_SMART_HOTEL_LANG');
		HOTEL_AUTH_INFO	= HOTEL_AUTH_INFO || $.mem.getVar('HISU_SMART_HOTEL_ROOM_INFO');//房间信息，开机认证时获得
		
		var msg = tmp.dataList[0];
		var tipWord = {zh:'消息通知',en:'Notice'};
		var btnMore = {zh:'查看更多',en:'View&nbsp;More'};
		var btnCancel = {zh:'取&nbsp;消',en:'Cancel'};
		var	hotelMsgDialog = $('hotel_msg_dialog');
		var msgParams = {
							hotelId : HOTEL_AUTH_INFO.hotelId,
							roomId : HOTEL_AUTH_INFO.roomId,
							messageId : msg.id
						};
		(new Image()).src = initRequestUrl('/SmartHotelInterface/api/smartHotel/readMessages',msgParams);

		var dialogHtml = '<div style="color:#ffb400; position:absolute; font-size:35px; left: 340px; top: 70px; width: 252px;" >'+tipWord[lang]+'</div>';
		dialogHtml += '<div style="color:#ffffff; position:absolute; font-size:28px; left: 46px; top: 148px; width: 657px; text-align:center; height: 180px;line-height:30px;overflow:hidden;" id="hotel_msg_content">'+msg.content.replace(/\\n/g,'<br/>')+'</div>';
		dialogHtml += '<div style="position:absolute; left: 144px; top: 352px; width:189px; height:55px; background:url(/smart_hotel/webpage/images/vod_bnt1.png) no-repeat center; color:#ffffff; font-size:27px; line-height:55px; text-align:center;" id="hotel_msg_dialog_btn0">'+btnMore[lang]+'</div>';
		dialogHtml += '<div style="position:absolute; left: 427px; top: 351px; width:189px; height:55px;background:url(/smart_hotel/webpage/images/vod_bnt0.png) no-repeat center; color:#626262; font-size:27px; line-height:55px; text-align:center;" id="hotel_msg_dialog_btn1">'+btnCancel[lang]+'</div>';

		if(hotelMsgDialog.e == null){
			var hotelMsgDiv = null;
			try{
				hotelMsgDiv = document.createElement('<div id="hotel_msg_dialog" style="position:absolute;	left:265px;	top:125px;	width:749px;height:469px; background:url(/smart_hotel/webpage/images/bind_bg.png) no-repeat center;-webkit-transition-duration: 200ms;visibility:hidden;z-index:500;">');
			}catch(e){
				hotelMsgDiv = document.createElement('div');
				hotelMsgDiv.setAttribute('id','hotel_msg_dialog');
				hotelMsgDiv.setAttribute('style','position:absolute;	left:265px;	top:125px;	width:749px;height:469px; background:url(/smart_hotel/webpage/images/bind_bg.png) no-repeat center;visibility:hidden;-webkit-transition-duration:200ms;z-index:500;');
			}
			$(hotelMsgDiv).html(dialogHtml);
			document.getElementsByTagName('body')[0].appendChild(hotelMsgDiv);
			hotelMsgDialog = $(hotelMsgDiv);
		}else{
			if($('hotel_msg_content').e==null){
				hotelMsgDialog.html(dialogHtml);
			}
			$('hotel_msg_content').text(msg.content.replace(/\\n/g,'<br/>'));
		}
		
		
		$.plugs.dialog.keyProcess = function(keyObj){ 
			if(focusArea!=$.plugs.dialog.focusArea)return 1;
			switch(keyObj.key){
				case "LEFT":
					if($.plugs.dialog.btnPos ==1){
						$('hotel_msg_dialog_btn0').bgImage("/smart_hotel/webpage/images/vod_bnt1.png").color('#ffffff');
						$('hotel_msg_dialog_btn1').bgImage("/smart_hotel/webpage/images/vod_bnt0.png").color('#626262');
						$.plugs.dialog.btnPos = 0;
					}
					break;
				case "RIGHT":
					if($.plugs.dialog.btnPos ==0){
						$('hotel_msg_dialog_btn0').bgImage("/smart_hotel/webpage/images/vod_bnt0.png").color('#626262');
						$('hotel_msg_dialog_btn1').bgImage("/smart_hotel/webpage/images/vod_bnt1.png").color('#ffffff');
						$.plugs.dialog.btnPos = 1;
					}
					break;
				case "BACK":
				case "EXIT":
					hotelMsgDialog.hide();
					focusArea = lastFocusArea;
					lastFocusArea = $.plugs.dialog.lastFocusArea;
					$.plugs.dialog.btnPos =0;
					$('hotel_msg_dialog_btn0').bgImage("/smart_hotel/webpage/images/vod_bnt1.png").color('#ffffff');
					$('hotel_msg_dialog_btn1').bgImage("/smart_hotel/webpage/images/vod_bnt0.png").color('#626262');
					if(pageUrl.indexOf('/portal/')>0){
						if(switchNoticeDelay)switchNoticeDelay();
					}
					break;
				case "OK":
					if($.plugs.dialog.btnPos ==1){
						$.plugs.dialog.keyProcess({key:'BACK'});
						setTimeout(hotelMsgNotice,HOTEL_NOTICE_INTERVAL*60*1000);
					}else{
						//跳转到酒店消息页面
						oHistory.add(window.location.href.split('?')[0]);
						location.href="/smart_hotel/webpage/hotelMessage.htm";
					}
					break;
			}
			return 0;
		};
		
		setTimeout(function(){
			if(pageUrl.indexOf('/portal/')>0){
				clearTimeout(switchNoticeTimer);
			}
			$.plugs.dialog.lastFocusArea = lastFocusArea;
			lastFocusArea = focusArea;
			focusArea = $.plugs.dialog.focusArea;
			hotelMsgDialog.show();
		},3000);
		
	}else{
		setTimeout(hotelMsgNotice,HOTEL_NOTICE_INTERVAL*60*1000);
	}
}

