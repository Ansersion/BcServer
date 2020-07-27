import pymysql
import xml.sax
import time
import sys
import hashlib

class TestDeviceConfigHandler( xml.sax.ContentHandler ):
   def __init__(self):
      self.CurrentData = ""
      # Device Element
      self.number = ""
      self.phoneNumber = ""

      # Mysql Element
      self.host = ""
      self.port = ""
      self.user = ""
      self.password = ""
      self.database = ""

   # 元素开始调用
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "user_client":
         print ("*****Test User Client*****")
      if tag == "mysql":
         print ("*****Mysql Para*****")

   # 元素结束调用
   def endElement(self, tag):
      if self.CurrentData == "number":
         print ("number:", self.number)
      elif self.CurrentData == "phone_number":
         print ("phoneNumber:", self.phoneNumber)
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
      if self.CurrentData == "number":
         self.number = content
      elif self.CurrentData == "phone_number":
         self.phoneNumber = content
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
# 2. create 'user' records
# 3. create 'user' and 'password' file 'user_password.dat'
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

    # 2. create 'user' records
    userList = []
    user2PwdDict = {}
    userNum = int(Handler.number)
    phoneNumber = int(Handler.phoneNumber)
    for i in range(userNum):
        pn = str(phoneNumber + i)
        # pwd = hashlib.sha256(pn.encode("utf-8")).hexdigest()
        pwd = pn + "PWD"
        userList.append(pn)
        user2PwdDict[pn] = pwd
        print(pn + "->" + user2PwdDict[pn])

    # 3. create 'user' and 'password' file 'user_password.dat'
    try:
        with open('user_password.dat', mode='w') as file:
            for userTmp in userList:
                file.write(userTmp + ":" + user2PwdDict[userTmp] + "\r\n")
    except Exception as e:
        print (e)
        sys.exit()


    # 4. insert the records into database
    # 打开数据库连接
    db = pymysql.connect(Handler.host,Handler.user,Handler.password,Handler.database )
    # 使用 cursor() 方法创建一个游标对象 cursor
    cursor = db.cursor()
    # 使用 execute()  方法执行 SQL 查询 
    for pn in userList:
        # insert sn_info
# INSERT INTO user_info
        sql = "insert into user_info (name, e_mail, phone, password) values(%s, %s, %s, %s)"
        # print(sql % (pn, pn, hashlib.sha256(user2PwdDict[pn].encode("utf-8")).hexdigest()))
        cursor.execute(sql, (pn, pn+"@beecom.online", pn, hashlib.sha256(user2PwdDict[pn].encode("utf-8")).hexdigest()))

    # commit 
    db.commit()
    # 关闭数据库连接
    db.close()


