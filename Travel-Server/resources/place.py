from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource
from config import Config
from mysql_connection import get_connection
from mysql.connector import Error
import boto3
from datetime import datetime


class placeResource(Resource):
    @jwt_required()
    def post(self) :

        # 1 클라이언트로부터 데이터 받아온다.
        option = request.form.get('option')
        region = request.form.get('region')
        placeName = request.form.get('placeName')
        content = request.form.get('content')
        file = request.files.get('image')
       
        user_id = get_jwt_identity()

        # 2. 사진을 s3에 저장한다.
        if file is None :
            return {'error' : '파일을 업로드 하세요'}, 400
        

        # 파일명을 회사의 파일명 정책에 맞게 변경한다.
        # 파일명은 유니크 해야 한다. 

        current_time = datetime.now()

        new_file_name = current_time.isoformat().replace(':', '_') + str(user_id) + '.jpg'  

        # 유저가 올린 파일의 이름을, 
        # 새로운 파일 이름으로 변경한다. 
        file.filename = new_file_name

        s3 = boto3.client('s3',
                    aws_access_key_id = Config.AWS_ACCESS_KEY_ID,
                    aws_secret_access_key = Config.AWS_SECRET_ACCESS_KEY )

        try :
            s3.upload_fileobj(file, 
                              Config.S3_BUCKET,
                              file.filename,
                              ExtraArgs = {'ACL' : 'public-read' , 
                                           'ContentType' : 'image/jpeg'} )
        except Exception as e :
            print(e)
            return {'error' : str(e)}, 500
        
        try :
            connection = get_connection()

            if option == '0':   #핫플인경우 

                query = '''insert into place
                        (userId, `option`,region,placeName,content,imgurl)
                        values
                        (%s, %s, %s,%s,%s,%s);'''
                
                record = (user_id,option,region,placeName,content,
                        Config.S3_LOCATION+new_file_name)
                
            else :  #축제인경우 

                strDate = request.form.get('strDate')
                endDate = request.form.get('endDate')

                strDate = datetime.strptime(strDate, "%Y-%m-%d")

                print("축제 시작 날짜:", strDate)  ##날짜 형식 2023-09-15

                endDate = datetime.strptime(endDate, "%Y-%m-%d")

                print("축제 끝나는 날짜:", endDate)  ##날짜 형식 2023-09-17

                query = '''insert into place
                        (userId, `option`,region,placeName,content,imgurl,strDate,endDate)
                        values
                        (%s, %s, %s,%s,%s,%s,%s,%s);'''
                
                record = (user_id,option,region,placeName,content,
                        Config.S3_LOCATION+new_file_name,strDate,endDate)

            
            cursor = connection.cursor()
            cursor.execute(query, record)

            connection.commit()
            cursor.close()
            connection.close()

        except Error as e:
            print(e)
            cursor.close()
            connection.close()
            return {'error' : str(e)}, 500

        return {'result' : 'success'}, 200
    
class placeListResource(Resource):
    @jwt_required()
    def get(self):
        user_id = get_jwt_identity()
        region = request.args.get('region')
        option = request.args.get('option')
        offset = request.args.get('offset')
        limit = request.args.get('limit')

        print(option)
        try:
            connection = get_connection()

            if option == '0':
                query = '''select id,region,placeName,imgUrl,content,createdAt
                            from place
                            where region = %s and place.option = 0
                            order by createdAt desc
                            limit '''+offset+''' , '''+limit+''' ;'''
                
            elif option == '1':
                query = '''select id,region,placeName,imgUrl,content,strDate,endDate,createdAt
                            from place
                            where region = %s and place.option = 1
                            order by createdAt desc
                            limit '''+offset+''' , '''+limit+''' ;'''
            
            record = (region,)
            cursor = connection.cursor(dictionary=True)
            cursor.execute(query,record)

            result_list = cursor.fetchall()
           
            print(result_list)
            

            cursor.close()
            connection.close()

        except Error as e:
            print(Error)
            cursor.close()
            connection.close()
            return{"ERROR" : str(e)},500 

        # 날짜 포맷 변경 

        # 날짜를 원하는 형식으로 문자열로 변환
   
        i = 0
        for row in result_list:
            result_list[i]['createdAt'] = row['createdAt'].isoformat().split("T")[0]
            result_list[i]['strDate'] = row['strDate'].isoformat().split("T")[0]
            result_list[i]['endDate'] = row['endDate'].isoformat().split("T")[0]
            i = i+1

        return {"result" : "success",
            "items" : result_list,
            "count" : len(result_list)},200
