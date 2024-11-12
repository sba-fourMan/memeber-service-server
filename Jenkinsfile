pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2' // ECR 리전 설정
        ECR_REPO_NAME = 'dev-member-service' // ECR 리포지토리 이름 설정
        IMAGE_TAG = "${env.BUILD_NUMBER}" // 이미지 태그 (빌드 번호)
        AWS_ACCOUNT_ID = '703671902880' // AWS 계정 ID
        ECR_REPO_URL = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}"
        EC2_INSTANCE_ID = '' // 생성한 EC2 인스턴스 ID를 저장할 변수
        PRIVATE_IP = ''
        SSH_KEY_PATH = 'dev-app.pem' // EC2 접속 키
        BASTION_HOST = '3.34.53.44' // Bastion Host의 공인 IP
        BASTION_SSH_KEY_PATH = '/dev-app.pem' // Bastion Host 접속 키
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/sba-fourMan/memeber-service-server', branch: 'dev'
            }
        }

        stage('Build JAR File') {
            steps {
                sh './gradlew build -x test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${ECR_REPO_URL}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                script {
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO_URL}"
                }
            }
        }

        stage('Push Docker Image to ECR') {
            steps {
                script {
                    sh "docker push ${ECR_REPO_URL}:${IMAGE_TAG}"
                }
            }
        }
    }

    post {
        always {
            sh "docker rmi ${ECR_REPO_URL}:${IMAGE_TAG} || true"
        }
    }
}
