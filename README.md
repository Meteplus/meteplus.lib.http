# meteplus.lib.http
    An efficient http library based on HttpURLConnection.
    it's very easy to use,and you can give a plan or error processing policy to your http request.
    this library is used in my many java spiders projects and runs well.
    
    
    public String searchcdr(String taskid){

        String postUrl=TeambtionServiceUrls.TB_Agent_Server_PINBAN+TeambtionServiceUrls.TB_PINBAN_SEARCH_TASK_BY_ID;
        return MPHttpClientCaller.doReusableHttpPost(postUrl,"taskid", taskid,this);
       
    }      
    
    public String search(String word,int index){

        String postUrl=TeambtionServiceUrls.TB_Agent_Server_PINBAN+TeambtionServiceUrls.TB_PINBAN_SEARCH_TASKS;
        HashMap<String,Object> paras=new HashMap<>();
        paras.put("word", word);
        paras.put("index", 0);
        return MPHttpClientCaller.doReusableHttpPost(postUrl, paras,this);
     
    }     

    public JSONObject checkTaskDinggaoSrcs(String wlshejiid){
            //TB_WLSheji_CHECK_TASK_DINGGAO_SRC    
        String postUrl=TeambtionServiceUrls.TB_Agent_Server+TeambtionServiceUrls.TB_WLSheji_CHECK_TASK_DINGGAO_SRC;
        return MPHttpClientCaller.doReusableHttpPostForJsonObject(postUrl,"shejiid", wlshejiid,this);
       
    }
    
    
    public Task getTaskInfo(String taskid){
        String postUrl=TeambtionServiceUrls.TB_Agent_Server+TeambtionServiceUrls.TB_WLSheji_GET_TASK;
        return Task.createInstanceByJson(MPHttpClientCaller.doReusableHttpPostForJsonObject(postUrl,"taskid", taskid,this));
    }
  
