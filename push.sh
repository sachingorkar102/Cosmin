echo pushing..
eval `ssh-agent`
ssh-add /home/sachin/homepc
git push
echo pushed