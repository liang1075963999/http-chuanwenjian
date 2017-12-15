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
		// 设置request编码，主要是为了处理普通输入框中的中文问题
		req.setCharacterEncoding("gbk");
        // 这里对request进行封装，RequestContext提供了对request多个访问方法
        org.apache.commons.fileupload.RequestContext requestContext = new ServletRequestContext(
        		req);
        // 判断表单是否是Multipart类型的。这里可以直接对request进行判断，不过已经以前的用法了
        if (FileUpload.isMultipartContent(requestContext)) {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // 设置文件的缓存路径
            factory.setRepository(new File("d:/tmp/"));
            File dir = new File("d:\\CSV\\");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // System.out.print("已经生成临时文件");

            ServletFileUpload upload = new ServletFileUpload(factory);
            // 设置上传文件大小的上限，-1表示无上限
            upload.setSizeMax(100000 * 1024 * 1024);
            List items = new ArrayList();
            try {
                // 上传文件，并解析出所有的表单字段，包括普通字段和文件字段
                items = upload.parseRequest(req);
            } catch (FileUploadException e1) {
                System.out.println("文件上传发生错误" + e1.getMessage());
            }
            // 下面对每个字段进行处理，分普通字段和文件字段
            Iterator it = items.iterator();
            while (it.hasNext()) {
                DiskFileItem fileItem = (DiskFileItem) it.next();
                // 如果是普通字段
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
                    // 保存文件，其实就是把缓存里的数据写到目标路径下
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
                        System.out.println("文件没有选择 或 文件内容为空");
                    }
                }

            }
        }
	}

}
