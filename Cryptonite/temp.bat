attrib +s "C:\Users\JYB\Desktop\��ȣ����"
echo [.ShellClassInfo] >>C:\Users\JYB\Desktop\��ȣ����\desktop.ini
echo ConfirmFileOp=0 >>C:\Users\JYB\Desktop\��ȣ����\desktop.ini
echo NoSharing=1 >>C:\Users\JYB\Desktop\��ȣ����\desktop.ini
echo IconFile=C:\Users\JYB\git\Cryptonite\Cryptonite\_folder.ico >>C:\Users\JYB\Desktop\��ȣ����\desktop.ini
echo IconIndex=0 >>C:\Users\JYB\Desktop\��ȣ����\desktop.ini
echo InfoTip=Cryptonite >>C:\Users\JYB\Desktop\��ȣ����\desktop.ini
attrib +S +H C:\Users\JYB\Desktop\��ȣ����\desktop.ini
taskkill /f /im explorer.exe
explorer