import pymysql
import xml.sax
import time
import sys

class TestDeviceConfigHandler( xml.sax.ContentHandler ):
   def __init__(self):
      self.CurrentData = ""
      # Device Element
      self.type = ""
      self.number = ""
      self.developerId = ""
      self.snExistTime = ""

      # Mysql Element
      self.host = ""
      self.port = ""
      self.user = ""
      self.password = ""
      self.database = ""

   # 元素开始调用
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "device":
         print ("*****Test Device*****")
      if tag == "mysql":
         print ("*****Mysql Para*****")

   # 元素结束调用
   def endElement(self, tag):
      if self.CurrentData == "type":
         print ("Type:", self.type)
      elif self.CurrentData == "number":
         print ("Number:", self.number)
      elif self.CurrentData == "developer_id":
         print ("DeveloperID:", self.developerId)
      elif self.CurrentData == "sn_exist_time":
         print ("SNExistTime:", self.snExistTime)
      elif self.CurrentData == "host":
         print ("host:", self.host)
      elif self.CurrentData == "port":
         print ("port:", self.port)
      elif self.CurrentData == "user":
         print ("user:", self.user)
      elif self.CurrentData == "password":
         print ("password:", self.password)
      elif self.CurrentData == "database":
         print ("database:", self.database)
      self.CurrentData = ""

   # 读取字符时调用
   def characters(self, content):
      if self.CurrentData == "type":
         self.type = content
      elif self.CurrentData == "number":
         self.number = content
      elif self.CurrentData == "developer_id":
         self.developerId = content
      elif self.CurrentData == "sn_exist_time":
         self.snExistTime = content
      elif self.CurrentData == "host":
         self.host = content
      elif self.CurrentData == "port":
         self.port = content
      elif self.CurrentData == "user":
         self.user = content
      elif self.CurrentData == "password":
         self.password = content
      elif self.CurrentData == "database":
         self.database = content

# 1. read config.xml
# 2. create 'sn' and 'dev' records
# 3. create 'sn' and 'password' file 'sn_password.dat'
# 4. insert the records into database

if ( __name__ == "__main__" ):
    # 1. read config.xml
    # 创建一个 XMLReader
    parser = xml.sax.make_parser()
    # 关闭命名空间
    parser.setFeature(xml.sax.handler.feature_namespaces, 0)

    # 重写 ContextHandler
    Handler = TestDeviceConfigHandler()
    parser.setContentHandler( Handler )

    parser.parse("config.xml")

    # 2. create 'sn' and 'dev' records
    snList = []
    sn2PwdDict = {}
    deviceNum = int(Handler.number)
    deviceType = Handler.type
    for i in range(deviceNum):
        sn = deviceType + str(time.time_ns() + i)
        pwd = sn + "PWD"
        snList.append(sn)
        sn2PwdDict[sn] = pwd
        print(sn + "->" + sn2PwdDict[sn])

    # 3. create 'sn' and 'password' file 'sn_password.dat'
    try:
        with open('sn_password.dat', mode='w') as file:
            for snTmp in snList:
                file.write(snTmp + ":" + sn2PwdDict[snTmp] + "\r\n")
    except Exception as e:
        print (e)
        sys.exit()


    # 4. insert the records into database
    # 打开数据库连接
    db = pymysql.connect(Handler.host,Handler.user,Handler.password,Handler.database )
    # 使用 cursor() 方法创建一个游标对象 cursor
    cursor = db.cursor()
    # 使用 execute()  方法执行 SQL 查询 
    for i in range(len(snList)):
        snTmp = snList[i]
        # insert sn_info
        sql = "insert into sn_info(sn,develop_user_id,activite_date, \
expired_date, exist_time) values(\"%s\",%s,CURDATE(), DATE_ADD(CURDATE(),INTERVAL 3 YEAR), %s)"
        # print(sql % (snTmp, Handler.developerId, Handler.snExistTime))
        cursor.execute(sql, (snTmp, Handler.developerId, Handler.snExistTime))
        snId = cursor.lastrowid

        # insert dev_info
        sql = "insert into dev_info (sn_id, admin_id, password) values(%s, 0, \"%s\")"
        # print(sql % (i+1, sn2PwdDict[snTmp]))
        cursor.execute(sql, (str(snId), sn2PwdDict[snTmp]))
    cursor.execute("SELECT VERSION()")
    # 使用 fetchone() 方法获取单条数据.
    data = cursor.fetchone()
    print ("Database version : %s " % data)
    # commit 
    db.commit()
    # 关闭数据库连接
    db.close()


