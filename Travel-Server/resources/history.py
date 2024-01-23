from flask import request
from flask_jwt_extended import get_jwt_identity, jwt_required
from flask_restful import Resource
from config import Config
from mysql_connection import get_connection
from mysql.connector import Error
import boto3
import openai
from datetime import datetime

openai.api_key = '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'


class planResource(Resource):

    def generate_text(self,prompt):
        response = openai.Completion.create(
        engine="gpt-3.5-turbo-instruct",
        prompt=prompt,
        temperature=0.5,
        max_tokens=4000,
        top_p=1,
        frequency_penalty=0,
        presence_penalty=0)
        return response.choices[0].text


    @jwt_required()
    def post(self) :
        data = request.get_json() 

        region = data['region']
        days = data['days']

      
        user_id = get_jwt_identity()

        keyward = days + '간의 ' + region + '여행 일정을 일일별로 계획해줘, 저녁 일정 소개 후 [next]를 붙여줘'

        plan = self.generate_text(keyward)

        plan = plan.split('[next]')

        for sentence in plan:
            print(sentence)

        try :
            connection = get_connection()

            if len(plan) == 1:
                query = '''insert into history
                    (userId, firstDay)
                    values
                    (%s, %s);'''
                record = (user_id,plan[0])

            elif len(plan) == 2:
                query = '''insert into history
                    (userId, firstDay,secondDay)
                    values
                    (%s, %s,%s);'''
                record = (user_id,plan[0],plan[1])
            elif len(plan) == 3:
                query = '''insert into history
                    (userId, firstDay,secondDay,thirdDay)
                    values
                    (%s, %s,%s,%s);'''
                record = (user_id,plan[0],plan[1],plan[2])

            elif len(plan) == 4:
                query = '''insert into history
                        (userId, firstDay,secondDay,thirdDay,fourthDay)
                        values
                        (%s, %s, %s,%s,%s);'''
                record = (user_id,plan[0],plan[1],plan[2],plan[3])

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

   

    
    
    

