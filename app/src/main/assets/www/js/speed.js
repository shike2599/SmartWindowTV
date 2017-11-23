//	iPanel.eventFrame.initPage(window);
	DVB.stopAV(0);
	DVB.clearVideoLevel();

	
	var firstPlay  = true;
	var playFlag   = false; //视频是否正常播放状态
	var exitFlag   = false; //提示退出操作
	var adShowFlag = false;
	var cableFlag  = true;
	var networkFlag = true;

	var playMedia    = {};
	var playBarFlag  = false;
	var playBarTime  = 5000;
	var playBarTimer = -1;
	
	window.E=iPanel.eventFrame;
	var volume = new window.E.volume();
	var currVolume = volume.getCurrVolume();
	var muteStatus = volume.getMuteStatus() //0表示未静音，1表示静音
	var volumeFlag = true;

	var startTime  = 0;
	var currTime   = -1;
	var dragTime   = -1;
	var totalTime  = 0;

	var seekPos    = 0;
	var seekValue  = [0,0,0,0,0,0];
	var seekStatus = false; //seek操作

	var exitPos  = 0;
	var exitInfo = [["确定","Yes"],["取消","No"],["温馨提示","Warm tips"]];
	var exitType = 0;
	var exitMsgs = [
					["是否终止观看?","Confirm eixt?"],
					["是否终止观看, 进入互动首页?","Confirm eixt, Enter portal?"],
					["尊敬的用户，您的暂停时间过长，是否终止观看?","Your pause for a long time, Confirm eixt?"]
					];

	var iconList = { 
			stop:    { id:"upIcon",    pos:0, pic:["ui://stop0.png","ui://stop1.png","ui://stop2.png"]}, 
			pause:   { id:"downIcon",  pos:0, pic:["ui://zt0.png","ui://zt1.png","ui://zt2.png"]},
			backward:{ id:"leftIcon",  pos:0, pic:["ui://left0.png","ui://left1.png","ui://left2.png"]}, 
			forward: { id:"rightIcon", pos:0, pic:["ui://right0.png","ui://right1.png","ui://right2.png"]}
	};

	var hideTimeor   = 0;
	var showTimeor   = -1;
	var warningTimor = -1;
	var networkTimor = -1;	
	
	var flg=0;//0代表不拽，1代表拽
	var cnt=0; 
	
	var playMedia    = {};
	
	function Media(){
		this.state = "";
		this.speed = 1;
		this.timer = -1;
		this.progName = "";
		this.rtspUrl  = "";
		this.wardFlag = false;
	}
	Media.prototype.open = function (){
	//	iPanel.debug("zhangb localfile vod rtsp = "+this.rtspUrl);	
		media.AV.open(this.rtspUrl,"VOD");
	}
	Media.prototype.replay = function(){
	//	iPanel.debug("zhangb replay");
		this.speed = 1; //media.AV.speed;
		this.state ="play";
		this.showFlag();	
		clearTimeout(this.timer);
		this.timer = window.setTimeout(function(){
			playMedia.wardFlag = false;
			media.AV.seek('00:00:00');
			dragTime=-1;
			//DVB.clearVideoLevel();
			currTime = 0;
			clearTimeout(showTimeor);
			showTimeor=setTimeout("refreshProgress();",1000);
		},1);
	}
	Media.prototype.play = function(){
	//	iPanel.debug("zhangb play");
		this.speed = 1;
		this.state ="play";
		if(!firstPlay) this.showFlag();
		clearTimeout(this.timer);
		this.timer = window.setTimeout(function(){
			playMedia.wardFlag = false;
			media.AV.play();
		},1);
	}
	Media.prototype.stop = function(){
	//	iPanel.debug("zhangb stop");
		this.speed = 1;
		this.state ="stop";
		this.showFlag();	
		clearTimeout(this.timer);
		this.timer = window.setTimeout(function(){
			playMedia.wardFlag = false;
			media.AV.seek('00:00:00');
			dragTime=-1;
			//DVB.clearVideoLevel();
			media.AV.pause();
			currTime = 0;
			clearTimeout(showTimeor);
		//	showTimeor=setTimeout("refreshProgress();",1000);
		},1);
	} 
	Media.prototype.pause = function(){
	//	iPanel.debug("zhangb pause");
	//	showAdsInfo();
		this.speed = 1;
		this.state ="pause";
		this.showFlag();	
		clearTimeout(this.timer);
		this.timer = window.setTimeout(function(){
			playMedia.wardFlag = false;
			media.AV.pause();
		},1);
	} 
	Media.prototype.seek = function(__time){
	//	iPanel.debug("zhangb seek time = "+__time);
		var tempTime = formatTime(__time);
	//	iPanel.debug("zhangb seek tempTime = "+tempTime);
		this.speed = 1;
		this.state ="play";
		this.showFlag();	
		clearTimeout(this.timer);
		playMedia.wardFlag = false;
		media.AV.seek(tempTime);
		//DVB.clearVideoLevel();
		currTime = __time;
		clearTimeout(showTimeor);
		showTimeor=setTimeout("refreshProgress();",1000);  //解决seek后media.AV.elapsed时间没有马上同步，导致进度条显示不正常；
	}
	Media.prototype.forward = function(){
	//	iPanel.debug("zhangb barFlag="+playBarFlag);	
		this.speed = this.speed<1 || this.speed*2>16 ? 1 : this.speed*2; //media.AV.speed;
		this.state = this.speed==1 ? "play" : "forward";
	//	iPanel.debug("zhangb forward speed = "+this.speed);
		this.showFlag();
		this.setWard();
	}
	Media.prototype.back = function(){
	//	iPanel.debug("zhangb back barFlag="+playBarFlag);
		this.speed = this.speed>1 || Math.abs(this.speed)*-2<-16 ? 1 : Math.abs(this.speed)*-2; //media.AV.speed;
		this.state = this.speed==1 ? "play" : "backward";
	//	iPanel.debug("zhangb back speed = "+this.speed);
		this.showFlag();
		this.setWard();
	}
	Media.prototype.setWard = function(){
		var self = this;
		var time = self.speed==1 ? 1 : (self.wardFlag ? 1 : 1);
		clearTimeout(this.timer);
		this.timer = window.setTimeout(function(){
			if(self.speed==1){
				self.wardFlag = false;
			//	iPanel.debug("zhangb media.AV.play()");
				media.AV.play();
				showPlayBar();
			}else if(self.speed>1){
				self.wardFlag = true;
			//	iPanel.debug("zhangb media.AV.forward("+ self.speed +")");
				media.AV.forward(self.speed);
				//DVB.clearVideoLevel();
			}else if(self.speed<1){
				self.wardFlag = true;
		//		iPanel.debug("zhangb media.AV.backward("+ self.speed +")");
				media.AV.backward(""+self.speed);
				//DVB.clearVideoLevel();
			}
		},time);
	}
	Media.prototype.getState = function(){
		//iPanel.debug("zhangb media.AV.status = " + media.AV.status);
		//this.state = media.AV.status;//应用自己来记录这个状态
		return this.state;
	}
	Media.prototype.getProName = function(){
		var tempNum = this.rtspUrl.lastIndexOf("/");
		this.progName = this.rtspUrl.substr(tempNum+1);
	//	iPanel.debug("zhangb progName ="+this.progName);
		return this.progName;
	}
    
	Media.prototype.showFlag = function(){
	//	iPanel.debug("zhangb status = "+this.getState());
	if(this.getState()!="pause"){ }//hideAdsInfo(); }
		switch(this.getState()){
			case "play":
				$("stateIcon").bgImage("ui://bfm.png");
				$("speedStep").html("");
				refreshIcon([0,0,0,0]);
				break;
			case "pause":
				$("stateIcon").bgImage("ui://ztm.png");
				$("speedStep").html("");
				refreshIcon([1,1,1,1]);
				break;
			case "forward":
				$("stateIcon").bgImage("ui://kfm.png");
				$("speedStep").html(""+this.speed);
				refreshIcon([1,1,0,0]);
				break;
			case "backward":
				$("stateIcon").bgImage("ui://mfm.png");
				$("speedStep").html(""+Math.abs(this.speed));
				refreshIcon([1,1,0,0]);
				break;
			case "stop":
				$("stateIcon").bgImage("ui://tzm.png");
				$("speedStep").html("");
				refreshIcon([1,1,1,1]);
				break;
		}
	}
	
	/**快进操作**/
	function FFSpeed(){
			if(exitFlag){
					changeExitItem(1);
				}else if(seekStatus){
				 	changeSeekItem(1);
				}else if(playFlag){
					switch(playMedia.getState()){
						case "play":
						case "forward":
						case "backward":
							flg=0;
							cnt=0;
							playMedia.forward();
							
							showPlayBar();
							clearTimeout(playBarTimer);
							keyDownEffect("forward");
							break;
						case "pause":
							flg=1;
							moveDragBar(60);
							break;
					}
			}
	}
	function FBSpeed(){
		if(exitFlag){
			changeExitItem(-1);
		}else if(seekStatus){
			changeSeekItem(-1);
		}else if(playFlag){
			switch(playMedia.getState()){
				case "play":
				case "forward":
				case "backward":
				    flag=0;
					cnt=0;
					playMedia.back();
					showPlayBar();
					clearTimeout(playBarTimer);
					keyDownEffect("backward");
					break;
				case "pause":
					flg=1;
					moveDragBar(-60);
					break;
		    }
		}					
	}
	
	
	function showPlayBar(){
		if(!playBarFlag){
			volume.hide();
			playBarFlag = true;
			$("stateIcon").show();
			$("speedStep").show();
			$("playBarDiv").show();
		}
		clearTimeout(playBarTimer);
		playBarTimer = setTimeout("hidePlayBar()", playBarTime);
	}
	
	function hidePlayBar(){
		if(playBarFlag&&playMedia.getState()!="pause"){
			playBarFlag = false;
			$("stateIcon").hide();
			$("speedStep").hide();
			$("playBarDiv").hide();
		}
	}
	
	function keyDownEffect(__type){
		var tempIcon = iconList[__type];
		$(tempIcon.id).src(tempIcon.pic[2]);
		window.setTimeout("$('"+ tempIcon.id +"').src('"+ tempIcon.pic[tempIcon.pos] +"');", 1000);
	}
	
	function showBarInfo(){
		if(currTime==-1) currTime = media.AV.elapsed;
		totalTime = media.AV.duration;
		$("endTime").html(formatTime(totalTime));
		refreshProgress();
	}
	
	function refreshProgress(){
		var status = playMedia.state;
		if(playFlag&&status!="pause"){
			//iPanel.debug("media.AV.elapsed= "+ media.AV.elapsed);		
			if(playMedia.wardFlag){
				hideTimeor = 0;
				var tempTime = media.AV.elapsed;
				if((status=="forward"&&currTime>tempTime)||(status=="backward"&&currTime<tempTime)) currTime++;
				else currTime = tempTime;
			}else if(!playBarFlag){
				hideTimeor++;
				var tempTime = media.AV.elapsed;
				currTime = Math.abs(currTime-tempTime)<3||hideTimeor>20 ? tempTime : currTime+1;
			}else if(status=="stop"){
				hideTimeor = 0;
				currTime = media.AV.elapsed;
			}else{
				hideTimeor = 0;
				var tempTime = media.AV.elapsed;
				if(currTime>tempTime) currTime++;
				else currTime = tempTime;
			}
			if(currTime>totalTime) currTime = totalTime;
			showProgress(currTime);
		}
		//clearTimeout(showTimeor);
		showTimeor=setTimeout("refreshProgress();",1000);
	}
	
	function showProgress(__time){
		var bar_length = Math.floor(parseInt(__time*448)/totalTime);
		if(bar_length <= 0) bar_length = 1;
		$("beginTime").html(formatTime(__time));
		$("progress").width(bar_length+"px");
	}

	function formatNums(__time){
		var tempArr = __time.split(":");
		return parseInt(tempArr[0],10)*3600 + parseInt(tempArr[1],10)*60 + parseInt(tempArr[2],10);
	}

	function formatTime(__time){
		var hour = Math.floor(__time/3600);
		var minute = Math.floor((__time%3600)/60);
		var second = __time - minute*60 - hour*3600;
		hour = hour < 10 ? "0"+hour:hour;
		minute = minute < 10 ? "0"+minute:minute;
		second = second < 10 ? "0"+second:second;
		return hour + ":" + minute + ":" + second;
	}

	function moveDragBar(__num){	
		dragTime = currTime;
		if(__num>0){		
			cnt=cnt+1;
		}else{
			cnt=cnt-1;
		}
		dragTime =dragTime+60*cnt;//60不管正负
		
	//	iPanel.debug("zhangb dragTime = "+dragTime);
		if(dragTime<startTime) {
			dragTime = startTime;
			warningMsg(lang?"movie start!":"已经到达影片开始位置");
		}else if(dragTime>totalTime) {
			dragTime = totalTime;	
			warningMsg(lang?"movie end!  ":"已经到达影片结束位置");
		}
		
		
		showProgress(dragTime);	
		
	}
	
	function selectEvent(){
	//	iPanel.debug("zhangb select seekStatus ="+seekStatus);
		if(exitFlag){
			hideExitNote();
			if(exitPos==0){
				confirmExit();
			}else{
				
			}
			return;			
		}	
		if(!playFlag) return;
		if(seekStatus){
			showPlayBar();
			var seektime = ""+seekValue[0] + seekValue[1] +":"+ seekValue[2] + seekValue[3] +":"+ seekValue[4] + seekValue[5];
		//	iPanel.debug("zhangb seektime="+seektime);
			seektime = formatNums(seektime);	
			if(seektime<startTime||seektime>totalTime){
				warningMsg(lang?"input overflow":"输入时间超出时长");
			}else{
			//	iPanel.debug("zhangb seekStatus seektime="+seektime);
				playMedia.seek(seektime);
			}
			hideSeekPanel();
			return;
		}
		var status = playMedia.getState();
	//	iPanel.debug("zhangb selectEvent status = " + status);
		if(status=='play'){
			flg=0;
			cnt=0;
			playMedia.pause();
			showPlayBar();
		}else if(status=='stop'||status=='pause'||status=='forward'||status=='backward'){
			//先在暂停状态拖拽，再快进快退按确认播放，这里dragTime有问题
			if(cnt==0){
				playMedia.play();
			}else{
				if(flg==0&&status!='pause'){
					playMedia.play();
				}else{
					playMedia.seek(dragTime);
				}
			}
			showPlayBar();
		}
	}
	
	function seekTT(){
		if(exitFlag){

		}else if(seekStatus){
			hideSeekPanel();
		}else if(playFlag){
			switch(playMedia.getState()){
				case "play":
				//case "forward":
				//case "backward":
				  showSeekPanel();
			  	  break;
				}
			}
	}
	
	///////////////////选时播放代码START///////////////////////

	function showSeekValue(){
		for(var i=0; i<seekValue.length; i++)$("num"+i).html(seekValue[i]);
	}

	function inputSeekNum(__num){
		seekValue[seekPos]=__num;
		$("num"+seekPos).html(seekValue[seekPos]);
		changeSeekItem(1);
	}

	function changeSeekNum(__num){
		var tempNum = parseInt(seekValue[seekPos],10);
		seekValue[seekPos] = ((tempNum+__num)%10+10)%10;
		$("num"+seekPos).html(seekValue[seekPos]);
	}

	function changeSeekItem(__num){
		setSeekStyle(0);
		var tempSize = seekValue.length;
		seekPos = ((seekPos+__num)%tempSize+tempSize)%tempSize;
		setSeekStyle(1);
	}

	function setSeekStyle(__type){
		$("num"+seekPos).bgImage( __type==0 ? "ui://seleT0.png" : "ui://seleT1.png");
	}

	function showSeekPanel(){
		if(seekStatus) return;
		var tempTime = formatTime(currTime).replace(/:/g, "");
	//	iPanel.debug("zhangb showSeekPanel tempTime = "+tempTime);
		volume.hide();
		seekStatus = true;	
		seekValue = tempTime.split("");
		showSeekValue();
		changeSeekItem(seekPos*-1);
		
		$("seekPanel").show();
	}

	function hideSeekPanel(){
		if(!seekStatus) return;
		seekStatus =false;
		$("seekPanel").hide();
	}
	
	function warningMsg(str){
		clearTimeout(warningTimor);
		showOverMain(str);
		warningTimor = setTimeout("hideOverMain()",2000);
	}
	
	function showOverMain(str){//提示DIV
		$("overMain").show();
		$("reminder").html(str);
	}

	function hideOverMain(){//隐藏提示DIV
		$("overMain").hide();
	}
	
	/////////////////音量控制代码START////////////////////

	function volumeAdjust(__offset) {
		if(playBarFlag){
			hidePlayBar();
		}
		if (muteStatus==1) muteStatus = volume.actionMute(0);
		if((__offset<0&&currVolume==0)||(__offset>0&&currVolume==99)){ volume.show(); return; }
		if(__offset > 0) volume.up(__offset, 99);
		else volume.down(__offset, 0);
		currVolume = volume.getCurrVolume();
		if(currVolume == 0 && __offset < 0){//cdq 添加这个是为了避免出现当显示的0时，还会出现有声音的问题
			volume.setVolume(currVolume);
		}
		showVolumeBar();
	}

	function showVolumeBar() {
	//	iPanel.debug("showVolumeBar = " + parseInt((currVolume/99)*31*23));
		$("vol_num").html(currVolume);
		$("volume_value").width(parseInt((currVolume/99)*31*23));
		if(currVolume==0&&volumeFlag){
			volumeFlag = false;
			$("vol_pic").style("opacity",0);//hide();
		}else if(currVolume>0&&!volumeFlag){
			volumeFlag = true;
			$("vol_pic").style("opacity",1);//show();
		}
	}
	
	/////////////////退出确认提示START//////////////////

	function initExitNote(){
		for(var i=0; i<exitInfo.length; i++) $("exitText"+i).html(exitInfo[i][lang]);
	}

	function showExitNote(__type){
		if(exitFlag) return;
		exitFlag = true;
		exitType = !__type ? 0 : __type;
		changeExitItem(exitPos*-1);
		$("exitText3").html(exitMsgs[exitType][lang]);
		$("overPanel").show();
	}

	function hideExitNote(){
		if(!exitFlag) return;
		exitFlag = false;
		$("overPanel").hide();
	}
	
	function changeExitItem(__num){
		$("exitText"+exitPos).bgImage("ui://focusText0.png");
		exitPos = ((exitPos+__num)%2+2)%2;
		$("exitText"+exitPos).bgImage("ui://focusText1.png");
	}
	
	function showCurrDate(){
		$("currDate").html(window.E.globalTimeFormat());
		window.setTimeout("showCurrDate()",60000);
	}
	
	function refreshIcon(__list){
		var tempArr = ["stop", "pause", "backward", "forward"];
		for(var i=0; i<tempArr.length; i++){
			var tempIcon = iconList[tempArr[i]];
			if(tempIcon.pos != __list[i]){
				tempIcon.pos = __list[i];
				$(tempIcon.id).src(tempIcon.pic[tempIcon.pos]);
			}
		}
	}
	
	function reset(){
		seekStatus=false;
		$("seekPanel").hide();
		playMedia.play();
	}
	
	function exitAndNew(){
		clearTimeout(showTimeor);
		clearTimeout(playBarTimer);
		media.AV.close();
		DVB.clearVideoLevel();
	}

