from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource
from config import Config
from mysql_connection import get_connection
from mysql.connector import Error
import boto3
from datetime import datetime

# 내 정보 관련
class UserInfoResource(Resource) :

    # 내 정보 수정
    @jwt_required()
    def put(self) :
        
        user_id = get_jwt_identity()
        profileImg = request.files.get("profileImg")
        name = request.form.get("name")

        # 프로필 사진만 변경할 때
        if profileImg is not None and profileImg.filename != '' and name == "" :
            current_time = datetime.now()

            new_file_name = current_time.isoformat().replace(':', '_') + str(user_id) + '.jpg'  

            profileImg.filename = new_file_name

            s3 = boto3.client('s3',
                        aws_access_key_id = Config.AWS_ACCESS_KEY_ID,
                        aws_secret_access_key = Config.AWS_SECRET_ACCESS_KEY )

            try :
                s3.upload_fileobj(profileImg, 
                                Config.S3_BUCKET,
                                profileImg.filename,
                                ExtraArgs = {'ACL' : 'public-read' , 
                                            'ContentType' : 'image/jpeg'} )
            except Exception as e :
                print(e)
                return {'error' : str(e)}, 500
            
            try :
                connection = get_connection()

                query = '''
                    update user
                    set profileImg = %s
                    where id = %s;
                    '''
                record = (Config.S3_LOCATION+new_file_name, user_id)
                cursor = connection.cursor()
                cursor.execute(query, record)
                connection.commit()

                cursor.close()
                connection.close()

            except Error as e :
                print(e)
                cursor.close()
                connection.close()
                return {"error" : str(e)}, 500

            return {"result" : "success"}, 200

        # 이름만 변경할 때
        elif profileImg.filename == '' and name != "" : 
            try :
                connection = get_connection()

                query = '''
                        update user
                        set name = %s
                        where id = %s;
                        '''
                record = (name, user_id)
                cursor = connection.cursor()
                cursor.execute(query, record)
                connection.commit()

                cursor.close()
                connection.close()

            except Error as e :
                print(e)
                cursor.close()
                connection.close()
                return {"error" : str(e)}, 500

            return {"result" : "success"}, 200
        
        # 프로필 사진, 이름 둘 다 변경할 때
        if profileImg is not None and profileImg.filename != '' and name != "" :
            current_time = datetime.now()

            new_file_name = current_time.isoformat().replace(':', '_') + str(user_id) + '.jpg'   

            profileImg.filename = new_file_name

            s3 = boto3.client('s3',
                        aws_access_key_id = Config.AWS_ACCESS_KEY_ID,
                        aws_secret_access_key = Config.AWS_SECRET_ACCESS_KEY )

            try :
                s3.upload_fileobj(profileImg, 
                                Config.S3_BUCKET,
                                profileImg.filename,
                                ExtraArgs = {'ACL' : 'public-read' , 
                                            'ContentType' : 'image/jpeg'} )
            except Exception as e :
                print(e)
                return {'error' : str(e)}, 500
            
            try :
                connection = get_connection()

                query = '''
                        update user
                        set name = %s, profileImg = %s
                        where id = %s;
                        '''
                record = (name, Config.S3_LOCATION+new_file_name, user_id)
                cursor = connection.cursor()
                cursor.execute(query, record)
                connection.commit()

                cursor.close()
                connection.close()

            except Error as e :
                print(e)
                cursor.close()
                connection.close()
                return {"error" : str(e)}, 500

            return {"result" : "success"}, 200
        
    # 내 정보 불러오기
    @jwt_required()
    def get(self) :

        user_id = get_jwt_identity()

        try :
            connection = get_connection()

            query = '''
                    select id, profileImg, name, email 
                    from user
                    where id = %s;
                    '''
            record = (user_id, )
            cursor = connection.cursor(dictionary=True)
            cursor.execute(query, record)

            result_list = cursor.fetchall()

            cursor.close()
            connection.close()

        except Error as e :
            print(e)
            cursor.close()
            connection.close()
            return {"error" : str(e)}, 500

        return {"result" : "success", "items" : result_list}, 200
    

    
