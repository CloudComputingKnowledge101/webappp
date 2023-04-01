packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.2"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

source "amazon-ebs" "amazonlinux" {
  ami_name      = "13032309-learn-packer-linux-aws"
  instance_type = "t2.micro"
  region        = "us-west-2"
  profile       = "dev"
  source_ami    = "ami-0f1a5f5ada0e7da53"
  access_key    = ""
  secret_key    = ""
  ssh_username  = "ec2-user"
  ami_users     = ["307582231222"]
  ami_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 50
    volume_type           = "gp2"
  }
}

build {
  name    = "learn-packer"
  sources = ["source.amazon-ebs.amazonlinux"]

  provisioner "file" {
    source      = "cloud-computing-assgn-1-0.0.1-SNAPSHOT.jar"
    destination = "cloud-computing-assgn-1-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = "ami.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    inline = ["sudo mkdir /home/ec2-user/webappp","sudo mkdir /home/ec2-user/webappp/statsd"]
  }

  provisioner "file" {
    source	= "cloudwatch-config.json"
    destination = "/tmp/"
   /* destination = "/home/ec2-user/webappp/statsd/cloudwatch-config.json" */
  }
  
  provisioner "shell" {
    inline = ["sudo mv /tmp/cloudwatch-config.json /home/ec2-user/webappp/statsd/cloudwatch-config.json"]
  }
  

  provisioner "shell" {
    inline = ["sleep 30", "sudo yum update -y", "sudo yum -y install java-17", "echo 'Install epel'", "sudo amazon-linux-extras install epel", "sleep 30"]
  }

  provisioner "shell" {
    inline = ["sleep 30", "sudo yum install amazon-cloudwatch-agent -y", "echo 'Installed cloudwatch'", "sleep 30"]
  }
  
  /*provisioner "shell" {
    inline = ["sleep 30","sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ec2-user/webappp/statsd/cloudwatch-config.json -s" , "sleep 30"]
  }*/

  provisioner "shell" {
    inline = ["echo '****** Moving amiservice! *******'", "sudo cp /tmp/ami.service /etc/systemd/system", "sudo chmod 755 /etc/systemd/system/ami.service", "sudo systemctl start ami.service", "sudo systemctl enable ami.service", "echo '****** Copied amiservice! *******'"]
  }
}