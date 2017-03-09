package com.ueueo.sample.log;

import android.os.Bundle;

import com.ueueo.log.UELog;
import com.ueueo.log.UELogLevel;
import com.ueueo.sample.AbsListActivity;

public class LogActivity extends AbsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UELog.init("UEUEO");
//        UELog.init("UEUEO", 5);
        UELog.init("UEUEO", 5, UELogLevel.INFO);
        UELog.init("UEUEO", 5, UELogLevel.INFO, true);

    }

    @Override
    public void initItemDatas() {
        addItemData(new ItemObject("日志") {
            @Override
            public void onItemClick() {
                User user = new User();
                user.id = 102;
                user.name = "UEUEO";
                user.age = 22;

                UELog.append("UELog日志特性展示");
                UELog.append("输出Json格式字符串");
                UELog.appendJson("{\"id\":221,\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
                UELog.append("输出Xml格式字符串");
                UELog.appendXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><html><title>this is a title</title><body>这个是网页</body></html>");
                UELog.append("输出对象");
                UELog.appendObject(user);
                UELog.i("输出日志");
            }
        });
        addItemData(new ItemObject("普通日志") {
            @Override
            public void onItemClick() {
                UELog.i("普通日志输出");
            }
        });
        addItemData(new ItemObject("设置输出方法调用栈数为3") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 3);

                UELog.i("输出方法数为3的日志");
            }
        });
        addItemData(new ItemObject("设置不输出方法调用栈") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);

                UELog.i("不输出方法调用栈的日志");
            }
        });
        addItemData(new ItemObject("输出Json字符串") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);

                UELog.json("{\"id\":221,\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
            }
        });
        addItemData(new ItemObject("输出Xml字符串") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);

                UELog.xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><html><title>this is a title</title><body>这个是网页</body></html>");
            }
        });
        addItemData(new ItemObject("输出对象") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);

                User user = new User();
                user.id = 102;
                user.name = "UEUEO";
                user.age = 22;

                UELog.object(user);
            }
        });
        addItemData(new ItemObject("输出错误信息") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);

                try {
                    Object obj = null;
                    obj.toString();
                } catch (Exception e) {
                    UELog.e(e, "空指针异常");
                }

            }
        });
        addItemData(new ItemObject("异步线程输出") {
            @Override
            public void onItemClick() {
                new Thread() {
                    @Override
                    public void run() {
                        UELog.init("UEUEO", 1);

                        UELog.i("异步线程输出日志");
                    }
                }.start();
            }
        });
        addItemData(new ItemObject("输出日志到本地文件中") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0, UELogLevel.VERBOSE);

                UELog.i("输出日志到本地文件中");
            }
        });
        addItemData(new ItemObject("自定义当前日志配置") {
            @Override
            public void onItemClick() {
                UELog.tag("LI").method(2).file(true).i("指定Tag为LI，方法调用栈显示2，日志写入文件");
            }
        });
        addItemData(new ItemObject("指定参数的日志") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 3);

                UELog.i("指定参数的日志输出  参数1:%d  参数2：%s   参数3：%s", 110, "apple", "ueueo");
            }
        });
        addItemData(new ItemObject("日志拼接合并输出") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);

                //打印请求地址
                UELog.i("POST  http://www.baidu.com/api/gps");
                //打印请求参数
                UELog.json("{\"id\":221}");
                //打印返回结果
                UELog.json("{\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");

                //输出混乱
                UELog.i("POST  http://www.baidu.com/api/1111");
                UELog.i("POST  http://www.baidu.com/api/2222");
                UELog.i("POST  http://www.baidu.com/api/3333");
                UELog.json("{\"id\":221}");
                UELog.json("{\"id\":12}");
                UELog.json("{\"name\":\"my name is zhangsan\",\"desc\":\"this is description 222!\"}");
                UELog.json("{\"id\":4321}");
                UELog.json("{\"name\":\"my name is lisi\",\"desc\":\"this is description  111!\"}");
                UELog.json("{\"name\":\"my name is wangwu\",\"desc\":\"this is description   2121!\"}");

                //拼接合并输出
                UELog.append("POST  http://www.baidu.com/api/gps");
                UELog.append("请求参数");
                UELog.appendJson("{\"id\":221}");
                UELog.append("返回结果");
                UELog.json("{\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
            }
        });
    }

    public class User {
        public int id;
        public String name;
        public int age;
    }
}
