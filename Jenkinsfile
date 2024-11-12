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
        stage('Check AWS Identity') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS Jenkins Access Key']]) {
                    sh 'aws sts get-caller-identity'
                }
            }
        }

        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/sba-fourMan/memeber-service-server', branch: 'dev'
            }
        }

        stage('Check or Provision EC2 Instance') {
            steps {
                script {
                    // 기존 인스턴스 ID를 조회하여 존재 여부 확인
                    def existingInstance = sh(script: """
                        aws ec2 describe-instances \
                            --region ${AWS_REGION} \
                            --filters "Name=tag:Name,Values=jenkins-container-runner" \
                            "Name=instance-state-name,Values=running" \
                            --query 'Reservations[*].Instances[*].InstanceId' \
                            --output text
                    """, returnStdout: true).trim()

                    if (existingInstance) {
                        echo "Existing EC2 Instance found: ${existingInstance}"
                        env.EC2_INSTANCE_ID = existingInstance
                    } else {
                        // 인스턴스가 없으면 새로 생성
                        def instanceDetails = sh(script: """
                            aws ec2 run-instances \
                                --region ${AWS_REGION} \
                                --image-id ami-03d31e4041396b53 \
                                --instance-type t2.micro \
                                --key-name Dev-App \
                                --security-group-ids sg-0e8f270c06f8639c5 \
                                --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=jenkins-container-runner}]' \
                                --query 'Instances[0].InstanceId' \
                                --output text
                        """, returnStdout: true).trim()
                        env.EC2_INSTANCE_ID = instanceDetails
                        echo "Created new EC2 Instance: ${env.EC2_INSTANCE_ID}"
                    }
                    // 사설 IP 가져오기
                    env.PRIVATE_IP = sh(script: """
                        aws ec2 describe-instances \
                            --instance-ids ${env.EC2_INSTANCE_ID} \
                            --query 'Reservations[0].Instances[0].PrivateIpAddress' \
                            --output text
                    """, returnStdout: true).trim()
                    echo "EC2 Instance ID: ${env.EC2_INSTANCE_ID}, Private IP: ${env.PRIVATE_IP}"
                }
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

        stage('Setup Docker on EC2 and Run Container via Bastion') {
            steps {
                script {
                    // Bastion Host를 통해 프라이빗 IP로 SSH 연결하여 Docker 설정 및 컨테이너 실행
                    sh """
                        ssh -o StrictHostKeyChecking=no -i ${BASTION_SSH_KEY_PATH} ec2-user@${BASTION_HOST} << EOF
                            ssh -o StrictHostKeyChecking=no -i ${SSH_KEY_PATH} ec2-user@${env.PRIVATE_IP} << 'INNER_EOF'
                                # Docker 설치
                                sudo yum install -y docker
                                sudo systemctl enable --now docker
                                sudo usermod -aG docker ec2-user

                                # ECR 로그인 및 컨테이너 실행
                                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO_URL}
                                docker pull ${ECR_REPO_URL}:${IMAGE_TAG}
                                docker run -d ${ECR_REPO_URL}:${IMAGE_TAG}
                            INNER_EOF
                        EOF
                    """
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
