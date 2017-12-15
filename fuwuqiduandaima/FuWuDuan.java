package ceshia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

@WebServlet("/FuWuDuan")
public class FuWuDuan extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// ����request���룬��Ҫ��Ϊ�˴�����ͨ������е���������
		req.setCharacterEncoding("gbk");
        // �����request���з�װ��RequestContext�ṩ�˶�request������ʷ���
        org.apache.commons.fileupload.RequestContext requestContext = new ServletRequestContext(
        		req);
        // �жϱ��Ƿ���Multipart���͵ġ��������ֱ�Ӷ�request�����жϣ������Ѿ���ǰ���÷���
        if (FileUpload.isMultipartContent(requestContext)) {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // �����ļ��Ļ���·��
            factory.setRepository(new File("d:/tmp/"));
            File dir = new File("d:\\CSV\\");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // System.out.print("�Ѿ�������ʱ�ļ�");

            ServletFileUpload upload = new ServletFileUpload(factory);
            // �����ϴ��ļ���С�����ޣ�-1��ʾ������
            upload.setSizeMax(100000 * 1024 * 1024);
            List items = new ArrayList();
            try {
                // �ϴ��ļ��������������еı��ֶΣ�������ͨ�ֶκ��ļ��ֶ�
                items = upload.parseRequest(req);
            } catch (FileUploadException e1) {
                System.out.println("�ļ��ϴ���������" + e1.getMessage());
            }
            // �����ÿ���ֶν��д�������ͨ�ֶκ��ļ��ֶ�
            Iterator it = items.iterator();
            while (it.hasNext()) {
                DiskFileItem fileItem = (DiskFileItem) it.next();
                // �������ͨ�ֶ�
                if (fileItem.isFormField()) {
                    System.out.println(fileItem.getFieldName()
                            + "   "
                            + fileItem.getName()
                            + "   "
                            + new String(fileItem.getString().getBytes(
                            "iso8859-1"), "gbk"));
                } else {
                    System.out.println(fileItem.getFieldName() + "   "
                            + fileItem.getName() + "   "
                            + fileItem.isInMemory() + "    "
                            + fileItem.getContentType() + "   "
                            + fileItem.getSize());
                    // �����ļ�����ʵ���ǰѻ����������д��Ŀ��·����
                    if (fileItem.getName() != null && fileItem.getSize() != 0) {
                        File fullFile = new File(fileItem.getName());
                        File newFile = new File("d:\\CSV\\"
                                + fullFile.getName());
                        try {
                            fileItem.write(newFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("�ļ�û��ѡ�� �� �ļ�����Ϊ��");
                    }
                }

            }
        }
	}

}
