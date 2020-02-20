package com.mvc.kiview.model.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.mvc.kiview.common.validate.FileValidate;
import com.mvc.kiview.common.validate.uploadFile;
import com.mvc.kiview.model.biz.CafeBiz;
import com.mvc.kiview.model.vo.CafeBoardVo;
import com.mvc.kiview.model.vo.CafeCategoryVo;
import com.mvc.kiview.model.vo.CafeMemberVo;
import com.mvc.kiview.model.vo.CafeMenuVo;
import com.mvc.kiview.model.vo.CafeVo;

@Controller // 카페관련
public class CafeController {

	@Autowired
	private FileValidate filevalidate;

	@Autowired
	private CafeBiz biz;

	//////////////////////////////// 카페 밖 /////////////////////////////////

	@RequestMapping("/cafehome.do")
	public String cafe_home(Model model, int member_no, String member_id) {

		List<CafeVo> Ulist = new ArrayList<CafeVo>();
		Ulist = biz.cafe_Ulist(member_no);

		List<CafeVo> Alist = new ArrayList<CafeVo>();
		Alist = biz.cafe_Alist(member_id);
		// 내가 가입한 카페 카페 목록
		model.addAttribute("Ulist", Ulist);

		// 내가 운영 중이 카페 목록
		model.addAttribute("Alist", Alist);

		return "cafe_home";
	}

	@RequestMapping("cafemy.do")
	public String cafe_my(Model model, int member_no) {

		List<CafeVo> cafe = biz.cafe_Ulist(member_no);
		List<CafeMemberVo> member = biz.member_selectAll(); 
		
		
		model.addAttribute("cafe", cafe);
		model.addAttribute("member",member);
		

		return "cafe_my";

	}
	
	@RequestMapping("/memberout.do")
	public void member_out(HttpServletResponse response, int member_no, int cafe_member_no) throws IOException {
		int res = biz.member_delete(cafe_member_no);
			
		response.setContentType("text/html charset=utf-8");
		PrintWriter out = response.getWriter();
			
		if(res>0) {
			out.print("<script> alert('카페를 탈퇴하였습니다.'); location.href='cafemy.do?member_no=" + member_no 
			+ "'</script>");
		} else {
			out.print("<script> alert('명령 실행 중 오류.'); location.href='cafemy.do?member_no=" + member_no 
					+ "'</script>");
			}
			
			
		}
	
	@RequestMapping("/cafedelete.do")
	public void cafe_delete(HttpServletResponse response, int member_no, int cafe_no) throws IOException {
		int res = biz.cafe_delete(cafe_no);
		
		response.setContentType("text/html charset=utf-8");
		PrintWriter out = response.getWriter();
			
		if(res>0) {
			out.print("<script> alert('카페를 폐쇄하였습니다.'); location.href='cafemy.do?member_no=" + member_no 
			+ "'</script>");
		} else {
			out.print("<script> alert('명령 실행 중 오류.'); location.href='cafemy.do?member_no=" + member_no 
					+ "'</script>");
		}
			
			
		
		
	}
	

	// ----------------------------카페 검색 -------------------------- ///
	@RequestMapping("/cafesearch.do")
	public String cafe_search(Model model, String keyword) {

		List<CafeVo> slist = biz.cafe_search(keyword);

		model.addAttribute("Slist", slist);
		model.addAttribute("keyword", keyword);
		return "cafe_search";
	}

	///////////////////////////// 카페 안 /////////////////////////////////
	// ----------------------------카페 디테일 -------------------------- ///

	public List sidebar(int cafe_no) {
		CafeVo cafe = biz.cafe_selectone(cafe_no);
		List<CafeMenuVo> menu = biz.menu_list(cafe_no);
		List<CafeMemberVo> member = biz.cafe_member_list(cafe_no);

		List list = new ArrayList();
		list.add(cafe);
		list.add(menu);
		list.add(member);

		return list;
	}

	@RequestMapping("/cafedetail.do")
	public String cafe_detail(HttpSession session, Model model, int cafe_no, int member_no) {
		// 상세페이지 카페 정보

		List list = sidebar(cafe_no);
		
		model.addAttribute("cafe_list", list);

		// 카페 회원 여부 확인 -> 버튼 변경 여부 확인 용
		CafeMemberVo regyn = new CafeMemberVo();
		regyn.setCafe_member_no(member_no);
		regyn.setCafe_no(cafe_no);

		CafeMemberVo res = biz.cafe_regyn(regyn);

		return "cafe_detail";
	}

	
	
	
	// --------------------- 내 카페 관리---------------------///
	@RequestMapping("/cafeconfig.do")
	public String cafe_config(Model model, int cafe_no) {
		List list = sidebar(cafe_no);
		model.addAttribute("cafe_list", list);
		
		
		
		CafeVo cafe = biz.cafe_selectone(cafe_no);
		model.addAttribute("vo", cafe);

		List<CafeMenuVo> menu = biz.menu_list(cafe_no);
		model.addAttribute("menu", menu);
		return "cafe_config";
	}
	
	

	@RequestMapping("/cafeupdate.do")
	public void cafe_update(HttpServletRequest request, HttpServletResponse response, CafeVo cafe, uploadFile file,
			BindingResult result) throws IOException {
		
	
		int res = biz.cafe_update(cafe);
		

		PrintWriter out = response.getWriter();
		response.setContentType("text/html; charset=utf-8");

		if (res > 0) {

			if (file.getFile1().getOriginalFilename().length()>0) {
				
				InputStream inputStream = null;
				OutputStream outputStream = null;

				try {
					MultipartFile thumb = file.getFile1();
					String thumb_name = cafe.getThumb();

					inputStream = thumb.getInputStream();

					String path = WebUtils.getRealPath(request.getSession().getServletContext(), "/storage");
					System.out.println("업로드 될 실제 경로 : " + path);

					File storage = new File(path);
					if (!storage.exists()) {
						storage.mkdir();
					}

					File newFile = new File(path + "/" + thumb_name);
					if (!newFile.exists()) {
						newFile.createNewFile();
					}

					outputStream = new FileOutputStream(newFile);
					int read = 0;

					byte[] b = new byte[(int) thumb.getSize()];
					while ((read = inputStream.read(b)) != -1) { // 업로드 하려는 파일 읽기
						outputStream.write(b, 0, read);

					}

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("thumb 수정 오류");
				} finally {
					inputStream.close();
					outputStream.close();
				}
			}

			if (file.getFile2().getOriginalFilename().length()>0) {
				InputStream inputStream = null;
				OutputStream outputStream = null;
				
				try {
					MultipartFile background = file.getFile2();
					String bg_name = cafe.getBackground();

					inputStream = background.getInputStream();

					String path = WebUtils.getRealPath(request.getSession().getServletContext(), "/storage");
					System.out.println("업로드 될 실제 경로 : " + path);

					File storage = new File(path);
					if (!storage.exists()) {
						storage.mkdir();
					}

					File newFile = new File(path + "/" + bg_name);
					if (!newFile.exists()) {
						newFile.createNewFile();
					}

					outputStream = new FileOutputStream(newFile);
					int read = 0;

					byte[] b = new byte[(int) background.getSize()];
					while ((read = inputStream.read(b)) != -1) { // 업로드 하려는 파일 읽기
						outputStream.write(b, 0, read);

					}

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("bg 수정 오류");
				} finally {
					inputStream.close();
					outputStream.close();
				}
			}

			out.print("<script> alert('변경사항이 적용되었습니다.'); location.href='cafeconfig.do?cafe_no=" + cafe.getCafe_no()
					+ "'</script> ");
		} else {

			out.print("<script> alert('입력사항을 확인해주세요.'); location.href='cafeconfig.do?cafe_no=" + cafe.getCafe_no()
					+ "'</script> ");
		}

	}

	//

	// --------------------- 게시판 관리 ---------------------//

	@RequestMapping("/menuinsert.do")
	public void menu_insert(HttpServletResponse response, CafeMenuVo vo, @RequestParam("category1") String cat1,
			@RequestParam("category2") String cat2, @RequestParam("category3") String cat3) throws IOException {

		

		int res = biz.menu_insert(vo, cat1, cat2, cat3);

		PrintWriter out = response.getWriter();
		response.setContentType("text/html; charset=UTF-8");

		if (res > 0) {

			out.print("<script> alert('게시판을 추가하였습니다.'); location.href='cafeconfig.do?cafe_no=" + vo.getCafe_no()
					+ "'</script> ");

		} else {

			out.print("<script> alert('입력사항을 확인해주세요.'); location.href='cafeconfig.do?cafe_no=" + vo.getCafe_no()
					+ "'</script> ");
			out.flush();

		}

	}

	@RequestMapping(value = "/menuchk.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Boolean> menu_chk(@RequestBody CafeMenuVo menu) {
		boolean check = false;
		CafeMenuVo vo = biz.menu_chk(menu);

		if (vo != null) {
			check = true;
		}

		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("check", check);

		return map;
	}

	@RequestMapping(value = "/menudetail.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> menu_detail(int no) {

		CafeMenuVo vo1 = biz.menu_detail1(no);
		List<CafeCategoryVo> vo2 = biz.menu_detail2(no);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("menu", vo1);
		map.put("category", vo2);

		return map;
	}

	@RequestMapping("/menudelete.do")
	public void menu_delete(HttpServletResponse response, int cafe_no, int cafe_menu_no) throws IOException {

		int res = 0;
		res = biz.menu_delete(cafe_menu_no);

		PrintWriter out = response.getWriter();
		response.setContentType("text/html; charset=UTF-8");

		if (res > 0) {

			out.print(
					"<script> alert('게시판을 삭제하였습니다.'); location.href='cafeconfig.do?cafe_no=" + cafe_no + "'</script>");

		} else {
			out.print(
					"<script> alert('입력사항을 확인해주세요.'); location.href='cafeconfig.do?cafe_no=" + cafe_no + "'</script>");
		}

	}

	@RequestMapping("/menuupdate.do")
	public void menu_update(HttpServletResponse response, CafeMenuVo menu, HttpServletRequest request)
			throws IOException {

		int category_no1 = Integer.parseInt(request.getParameter("category_no1"));
		String category1 = request.getParameter("category1").trim();
		int category_no2 = Integer.parseInt(request.getParameter("category_no2"));
		String category2 = request.getParameter("category2").trim();
		int category_no3 = Integer.parseInt(request.getParameter("category_no3"));
		String category3 = request.getParameter("category3").trim();

		int res = 0;
		res = biz.menu_update(menu);

		PrintWriter out = response.getWriter();
		response.setContentType("text/html charset=utf-8");

		if (res == 0) {
			out.print("<script> alert('게시판 이름을 확인하세요.'); location.href='cafeconfig.do?cafe_no=" + menu.getCafe_no()
					+ "'</script>");
		}

		CafeCategoryVo category = new CafeCategoryVo();

		if (category_no1 > 0) {
			

			if (category1.length() <= 1) {

				res += biz.category_delete(category_no1);

			} else if (category1.length() > 1) {

				category.setCafe_category_no(category_no1);
				category.setCategory(category1);
				res += biz.category_update(category);
			}

		} else if (category_no1 == 0) {

			if (category1.length() > 1) {

				category.setCafe_menu_no(menu.getCafe_menu_no());
				category.setCategory(category1);
				res += biz.category_update_insert(category);
			}
		}

		if (category_no2 > 0) {

			if (category2.length() <= 1) {

				res += biz.category_delete(category_no2);
			} else if (category2.length() > 1) {

				category.setCafe_category_no(category_no2);
				category.setCategory(category2);
				res += biz.category_update(category);

			}

		} else if (category_no2 == 0) {

			if (category2.length() > 1) {

				category.setCafe_menu_no(menu.getCafe_menu_no());
				category.setCategory(category2);
				res += biz.category_update_insert(category);
			}
		}

		if (category_no3 > 0) {

			if (category3.length() <= 1) {

				res += biz.category_delete(category_no3);
			} else if (category3.length() > 1) {

				category.setCafe_category_no(category_no3);
				category.setCategory(category3);
				res += biz.category_update(category);
			}
		} else if (category_no3 == 0) {

			if (category3.length() > 1) {

				category.setCafe_menu_no(menu.getCafe_menu_no());
				category.setCategory(category3);
				res += biz.category_update_insert(category);
			}
		}

		if (res > 0) {
			out.print("<script> alert('변경 사항이 적용되었습니다.'); location.href='cafeconfig.do?cafe_no=" + menu.getCafe_no()
					+ "'</script>");
		} else {
			out.print("<script> alert('입력사항을 확인해주세요.'); location.href='cafeconfig.do?cafe_no=" + menu.getCafe_no()
					+ "'</script>");
		}

	}
	
	
	// ----------------------- 회원 관리  ----------------------------//
	@RequestMapping("/memberblock.do")
	@ResponseBody
	public Map member_block(int cafe_no, int cafe_member_no) {
		int res = biz.member_block(cafe_member_no);
		
		
		List<CafeMemberVo> list = biz.cafe_member_list(cafe_no);
		Map map = new HashMap();
		
		for(int i=0; i<list.size(); i++) {		
			map.put(i, list.get(i));
	
		}
		
		
		return map;
	}
	
	@RequestMapping("/memberunblock.do")
	@ResponseBody
	public Map member_unblock(int cafe_no, int cafe_member_no) {
		int res = biz.member_unblock(cafe_member_no);
				
		List<CafeMemberVo> list = biz.cafe_member_list(cafe_no);
		Map map = new HashMap();
		
		for(int i=0; i<list.size(); i++) {		
			map.put(i, list.get(i));
	
		}
		
		System.out.println(map);
		return map;
	}

	
	@RequestMapping("/membercancle.do")
	@ResponseBody
	public Map member_cancle(int cafe_no, int cafe_member_no) {
		int res= biz.member_delete(cafe_member_no);
		
		List<CafeMemberVo> list = biz.cafe_member_list(cafe_no);
		Map map = new HashMap();

		for(int i=0; i<list.size(); i++) {		
			map.put(i, list.get(i));
	
		}
		
		
		return map;
	}
	
	@RequestMapping("membersign.do")
	@ResponseBody
	public Map member_sign(int cafe_no, int cafe_member_no) {
		int res = biz.member_sign(cafe_member_no);
		
		List<CafeMemberVo> list = biz.cafe_member_list(cafe_no);
		Map map = new HashMap();
		
		for(int i=0; i<list.size(); i++) {
			map.put(i, list.get(i));
		}
		
		return map;
	}
	
	
	
	
	
	
	
	

	// ------------------------ 카페 회원가입---------------------------//

	@RequestMapping("/cafejoinform.do")
	public String cafe_joinbefore(Model model, int cafe_no) {
		List list = sidebar(cafe_no);
		model.addAttribute("cafe_list", list);

		

		return "cafe_join";
	}

	@RequestMapping("/cafejoin.do")
	public String cafe_join(Model model, int cafe_no, CafeMemberVo member) {


		int res = biz.cafe_join(member);

		if (res > 0) {
			System.out.println("가입을 신청하셨습니다.");
			return "redirect:cafedetail.do?cafe_no=" + cafe_no + "&member_no="+ member.getMember_no();

		} else {
			System.out.println("입력사항을 확인해주세요.");
			return "redirect:cafejoinform.do?cafe_no=" + cafe_no;
		}
		
		
	}

	// ------------------------카페 오픈---------------------------//

	@RequestMapping("/cafeopen.do")
	public String cafe_open() {
		return "cafe_open";
	}

	@RequestMapping("/cafeinsert.do")
	public String cafe_insert(HttpServletRequest request, Model model, CafeVo cafe, uploadFile uploadfile,
			@RequestParam("member_no") int member_no, @RequestParam("admin") String admin, BindingResult result) throws FileNotFoundException {
		

		filevalidate.validate(uploadfile, result);

		if (result.hasErrors()) { // 에러가 발생하였는지

			return "cafe_open";
		}

		int count1 = 1;
		int count2 = 1;

		MultipartFile thumb = uploadfile.getFile1(); // 업로드한 파일
		MultipartFile background = uploadfile.getFile2(); // 업로드한 파일
		String thumb_name = "thumb" + count1 + ".jpg";
		String bg_name = "bg" + count2 + ".jpg";

		uploadFile file = new uploadFile();

		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		OutputStream outputStream1 = null;
		OutputStream outputStream2 = null;

		try {
			inputStream1 = thumb.getInputStream();
			inputStream2 = background.getInputStream();

			// 경로 설정
			String path = WebUtils.getRealPath(request.getSession().getServletContext(), "/storage");
			System.out.println("업로드 될 실제 경로 : " + path);

			// 파일경로 존재확인
			File storage = new File(path);

			if (!storage.exists()) { // 존재여부
				storage.mkdirs(); // 해당 경로를 만들겠다.
			}

			// 썸네일 파일명 중복확인

			File newfile1 = new File(path + "/" + thumb_name);

			while (newfile1.exists()) {
				count1++;
				System.out.println("이미지1 이름 중복 : " + count1);
				thumb_name = "thumb" + count1 + ".jpg";
				newfile1 = new File(path + "/" + thumb_name);

				if (!newfile1.exists()) {
					newfile1.createNewFile();
					break;
				}

			}

			// 배경이미지 파일명 중복확인
			File newfile2 = new File(path + "/" + bg_name);

			while (newfile2.exists()) {
				count2++;
				System.out.println("이미지2 이름 중복 : " + count2);
				bg_name = "bg" + count2 + ".jpg";
				newfile2 = new File(path + "/" + bg_name);

				if (!newfile2.exists()) {
					newfile2.createNewFile();
					break;
				}
			}

			//
			outputStream1 = new FileOutputStream(newfile1);
			int read1 = 0;
			byte[] b1 = new byte[(int) thumb.getSize()]; // outputStream 은 byte단위이기 떄문

			while ((read1 = inputStream1.read(b1)) != -1) { // 업로드 하려는 파일 읽기
				outputStream1.write(b1, 0, read1);

			}

			outputStream2 = new FileOutputStream(newfile2);
			int read2 = 0;
			byte[] b2 = new byte[(int) background.getSize()]; // outputStream 은 byte단위이기 떄문

			while ((read2 = inputStream2.read(b2)) != -1) { // 업로드 하려는 파일 읽기
				outputStream2.write(b2, 0, read2);

			}

		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			try {
				inputStream1.close();
				inputStream2.close();
				outputStream1.close();
				outputStream2.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		CafeVo vo1 = new CafeVo(cafe.getTitle(), admin, cafe.getIntro(), thumb_name, bg_name, cafe.getRestriction(),
				cafe.getQuestion());

		CafeMemberVo vo2 = new CafeMemberVo(member_no, admin,"카페장", "A");

		int res = 0;
		res = biz.cafe_insert(vo1, vo2);

		if (res > 0) {
			return "redirect:cafehome.do?member_no=" + member_no + "&member_id=" + admin;
		
		} else {
			
			String path = WebUtils.getRealPath(request.getSession().getServletContext(), "/storage");
			File bg_delete = new File(path+"/"+bg_name);
			
			bg_delete.delete();
			
			File thumb_delete = new File(path+"/"+thumb_name);
			
			thumb_delete.delete();
			
			return "redirect:cafeopen.do";
		}

	}
	
	
	@RequestMapping("/cafechk.do")
	@ResponseBody
	public Map<String, Boolean> cafe_chk(String title) {
		
		
		CafeVo cafe = biz.cafe_chk(title);
		
		Map<String, Boolean> map = new HashMap();
		if(cafe==null) {
			map.put("check", true);
			
		} else {
			map.put("check", false);
		}
		
		return map;
	}

	// -------------------------- 카페 게시판 --------------------//
	@RequestMapping("/cafeboardlist.do")
	public String cafe_board_list(Model model, int cafe_no, int cafe_menu_no) {
		List list = sidebar(cafe_no);
		model.addAttribute("cafe_list", list);

		String cafemenuname = biz.cafe_menu_name(cafe_menu_no);
		List<CafeBoardVo> Blist = biz.cafe_boardlist(cafe_menu_no);
		model.addAttribute("Blist", Blist);
		model.addAttribute("cafe_menu_name", cafemenuname);
		model.addAttribute("cafe_menu_no", cafe_menu_no);  
		return "cafe_board";
	}

	@RequestMapping("/boardwrite.do")
	public String board_write(Model model, int cafe_no, int  cafe_menu_no) {
		List list = sidebar(cafe_no);
	    model.addAttribute("cafe_list", list );
	     
	     
	    model.addAttribute("cafe_menu_no",cafe_menu_no);
		
		return "cafe_board_write";
	}

	@RequestMapping(value = "/ajaxcategory.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> ajaxcategory(int no) {

		

		List<CafeCategoryVo> reslist = biz.menu_detail2(no);
		// System.out.println(reslist);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("category2", reslist);

		/*
		 * for(String key : map.keySet()) { String value = map.get(key).toString();
		 * System.out.println("[key] : "+key+"[value] : "+value); }
		 */
		return map;
	}
	
	
	@RequestMapping("/cafeboardinsert")
	   public String cafe_board_insert(String title,String content,String category, String writer, int cafe_menu_no, int cafe_no) {
	      
	      
	      CafeBoardVo cafeboardvo = new CafeBoardVo();
	      cafeboardvo.setCafe_menu_no(cafe_menu_no);
	      cafeboardvo.setCategory(category.trim());      
	      cafeboardvo.setTitle(title);
	      cafeboardvo.setWriter(writer);
	      cafeboardvo.setContent(content);
	            
	      int res = biz.cafe_board_insert(cafeboardvo);   
	      
	                  
	       if(res>0) {
	         
	          return "redirect:cafeboardlist.do?cafe_no="+cafe_no+"&cafe_menu_no="+cafe_menu_no;
	           
	       }else {
	             
	          return "redirect:cafeboardwriteform?cafe_no="+cafe_no+"cafe_menu_no="+cafe_menu_no;
	       }
	      
	   }
	   @RequestMapping("/boarddetail.do")
	   public String board_detail(Model model, int cafe_board_no , int cafe_menu_no, int cafe_no) {
	      List list = sidebar(cafe_no);  
	      model.addAttribute("cafe_list", list );
	      
	      CafeBoardVo cafeboardvo = biz.cafe_board_detail(cafe_board_no);
	      model.addAttribute("cafe_board_detail",cafeboardvo);      
	      model.addAttribute("cafe_menu_no",cafe_menu_no);
	      
	      return "cafe_board_detail";
	   }
	   @RequestMapping("/cafeboarddelete.do")
	   public String cafe_board_delete(Model model,int cafe_board_no , int cafe_menu_no,int cafe_no) {
	      int res = biz.cafe_board_delete(cafe_board_no);
	      
	      System.out.println(res);
	      if(res>0) {
	            return "redirect:cafeboardlist.do?cafe_no="+cafe_no+"&cafe_menu_no="+cafe_menu_no;
	      }else {
	         return "redirect:cafeboarddetail.do?cafe_no="+cafe_no+"&cafe_menu_no="+cafe_menu_no+"&cafe_board_no="+cafe_board_no;
	      }      
	      
	   }
	   @RequestMapping("/cafeboardupdateform.do")
	   public String cafe_board_updateform(Model model,int cafe_board_no,int cafe_no,int cafe_menu_no) {
	      CafeBoardVo cafeboardvo = biz.cafe_board_detail(cafe_board_no);
	      
	      List list = sidebar(cafe_no); 
	      model.addAttribute("cafe_list", list );
	      
	      model.addAttribute("cafeboardvo",cafeboardvo);
	      model.addAttribute("cafe_no",cafe_no);
	      model.addAttribute("cafe_menu_no",cafe_menu_no);
	      model.addAttribute("cafe_board_no",cafe_board_no);
	      return "cafe_board_update";
	   }
	   
	   
	   @RequestMapping("/cafeboardupdate.do")
	   public String cafe_board_update(Model model,int cafe_no,CafeBoardVo board) {
	      List list = sidebar(cafe_no); 
	      model.addAttribute("cafe_list", list );
	      
	         int res = biz.cafe_board_update(board); 
	         System.out.println(board);
	         
	         if(res>0) {
	            return "redirect:boarddetail.do?cafe_no="+cafe_no+"&cafe_menu_no="+board.getCafe_menu_no()+"&cafe_board_no="+board.getCafe_board_no();
	         }else {
	            return "redirect:cafeboardupdateform.do?cafe_no="+cafe_no+"&cafe_menu_no="+board.getCafe_menu_no()+"&cafe_board_no="+board.getCafe_board_no();
	         }
	      
	   }
	
	
	
	
}