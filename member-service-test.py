from locust import HttpUser, task, between, TaskSet, events
import random
import string

HOST = "http://localhost:7070"

def generate_user_id():
    """고유한 사용자 ID를 생성하는 함수"""
    return ''.join(random.choices(string.ascii_letters + string.digits, k=8))

def generate_password():
    """유효한 비밀번호를 생성하는 함수 (영문자와 숫자 포함)"""
    lowercase = random.choice(string.ascii_lowercase)
    uppercase = random.choice(string.ascii_uppercase)
    digit = random.choice(string.digits)
    remaining_length = 12 - 3
    remaining_characters = ''.join(random.choices(string.ascii_letters + string.digits, k=remaining_length))
    password = lowercase + uppercase + digit + remaining_characters
    return ''.join(random.sample(password, len(password)))

class UserBehavior(TaskSet):
    def on_start(self):
        """사용자 시작 시 고유한 ID와 비밀번호 생성"""
        self.user_id = generate_user_id()
        self.password = generate_password()
        self.signup_successful = False
        self.access_token = None

    @task(1)  # 회원가입 API 호출
    def signup(self):
        signup_data = {
            "signUpId": self.user_id,
            "password": self.password,
            "userRole": "BUYER",
        }
        with self.client.post("/members/signup", json=signup_data, headers={"Content-Type": "application/json"}, catch_response=True) as response:
            if response.status_code == 400:
                print(f"Signup failed for {self.user_id}: {response.text}")
                # 아이디가 중복된 경우 새로운 ID 생성 후 재시도
                self.user_id = generate_user_id()
                self.signup()
            elif response.status_code == 200:
                print(f"Signup successful for {self.user_id}: {response.text}")
                self.signup_successful = True
                self.signin()  # 회원가입 후 즉시 로그인 시도

    @task(2)  # 로그인 API 호출
    def signin(self):
        if not self.signup_successful:
            print(f"Signin skipped for {self.user_id} because signup was not successful.")
            return  # 회원가입이 실패한 경우 로그인 시도하지 않음

        signin_data = {
            "signInId": self.user_id,
            "password": self.password
        }
        with self.client.post("/members/signin", json=signin_data, headers={"Content-Type": "application/json"}, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Signin failed for {self.user_id}: {response.text}")
            else:
                print(f"Signin successful for {self.user_id}: {response.text}")
                self.access_token = response.json().get("accessToken")
                self.charge_points(100)

    @task(3)  # 포인트 충전 API 호출
    def charge_points(self, amount):
        if not self.access_token:
            print(f"Charge points skipped for {self.user_id} because there is no access token.")
            return

        charge_data = {
            "amount": amount
        }
        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
        with self.client.post("/members/points/charge", json=charge_data, headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Charge points failed for {self.user_id}: {response.text}")
            else:
                print(f"Charge points successful for {self.user_id}: {response.text}")

    @task(4)  # 로그아웃 API 호출
    def signout(self):
        if not self.access_token:
            print(f"Signout skipped for {self.user_id} because there is no access token.")
            return

        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
        with self.client.post("/members/signout", headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Signout failed for {self.user_id}: {response.text}")
            else:
                print(f"Signout successful for {self.user_id}: {response.text}")

class WebsiteUser(HttpUser):
    host = HOST
    tasks = [UserBehavior]
    wait_time = between(1, 3)

@events.request.add_listener
def request_handler(request_type, name, response_time, response_length, response, exception, **kwargs):
    if exception:
        print(f"Request to {name} failed with exception: {exception}")
    else:
        print(f"Successfully made a request to: {name} with response time: {response_time}ms")
