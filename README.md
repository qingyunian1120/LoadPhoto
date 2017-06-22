# LoadPhoto
第一次提交小结：
    1.AsyncTask中，只有doBackground()方法是处于子线程中运行的，其他三个回调函数onPreExecute()、onPostExecute()、
   onProgressUpdate()都是在UI线程中进行，因此在这三个方法里面可以进行UI更新的工作；
    2.每一个new出来的AsyncTask只能执行一次execute方法，多次运行将会报错，如需多次，需要new一个AsyncTask；
    3.AsyncTask必须在UI线程中创建实例，execute()方法也必须在UI线程中调用;
