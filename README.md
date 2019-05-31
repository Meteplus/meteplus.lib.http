# meteplus.lib.http
    An efficient http client library based on HttpURLConnection.
    it's very easy to use,and you can give a plan or error processing policy to your http request.
    this library has been used in my many java spiders projects, it runs well
    
    the following are some use cases:
    
    public String searchcdr(String taskid){

        String url="http://xxx...";
        return MPHttpClientCaller.doReusableHttpPost(url,"taskid", taskid,this);
       
    }      
    
    public String search(String word,int index){

        String url="http://xxx...";
        HashMap<String,Object> paras=new HashMap<>();
        paras.put("word", word);
        paras.put("index", 0);
        return MPHttpClientCaller.doReusableHttpPost(url, paras,this);
     
    }     

    public JSONObject checkTaskDinggaoSrcs(String wlshejiid){   
        String url="http://xxx...";
        return MPHttpClientCaller.doReusableHttpPostForJsonObject(url,"shejiid", wlshejiid,this);
       
    }
    
    
    public Task getTaskInfo(String taskid){
        String url="http://xxx...";
        return Task.createInstanceByJson(MPHttpClientCaller.doReusableHttpPostForJsonObject(url,"taskid", taskid,this));
    }
  
