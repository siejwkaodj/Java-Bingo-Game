package java_bingo_game;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * @author s_hsj0209
 *	TODO : 
 *	버그 리스트 : 
 *	
 *
 *	4. 게임 실행 중에 창 x표 눌러서 닫으면 그냥 둘 다 닫히는 것 -> windowevent 처리 필요
 *	-> 221208 해결
 *
 *	5. 3*3 등 단어장 크기보다 작은 크기로 게임을 할 때 (서로 빙고판 다른 거 있을 때) userBoard에만 체크 몰빵되는 현상
 *	-> 그냥 단어 겹치면서 일어나는 현상. getRandomWord() 메소드 수정 필요.
 *	-> 안남
 *
 *	--------------- solved
 *	1. 
 *	이름 입력 한글로 받으면 FileManager.java의 updateStatistics()에서 이제까지 있었던 result파일
 *	그냥 자기 알아서 초기화해버리는 버그 있음.
 *	이름도 null, 다른 모든 변수들 0으로 바뀜. 무조건 초기화 안 된 상태로 바뀌는듯.
 *	updateStatistics()에선 현재 userWinCnt + 100으로 파일 설정 -> 이부분 참고해서 디버깅 해볼 것
 *	-> 사용자 이름 부분 없앰
 *
 *	6. com 빙고 현황 정보판에 com의 빙고 상황이 제대로 표시되지 않는 버그
 *	-> 해결, textInput actionEvent쪽에서 순서 잘못 설정되어 있었음.
 *
 *	2. 
 *	통계 버튼 처음 눌렀을 때 -> initStatistics()에서 설정한 대로 일단 설정, 그리고 버튼 한번 더 누르면 itemListener()에 설정한 대로
 *	출력이 됨.
 *	그리고 게임 끝나고 바로 통계 조회 누르면 처음 눌렀을 때는 업데이트 안되고, 두번째 눌렀을 때 업뎃이 됨. 심지어 게임 끝나고 나오는 정보는 제대로 나옴.
 *	-> 221208 해결, 갱신하는 함수를 다른 버튼 이벤트에 잘못 달아줬었음
 *
 *	3. GameFrame 첫줄에 있는 단어들은 입력하면 userBoards()에는 표시가 되는데, 프레임에는 있다고 인식을 하지 못함(?)
 *	그래서 없다고 오류는 뜨는데 정작 판정은 제대로 됨.
 *	-> 221208 해결, y > 0으로 설정한 문제였음
 *
 *	4. 빙고 판정 제대로 되지 않는 문제 - / 대각선 했을땐 제대로 됨(3번버그 나오면서) 근데 4*4 빙고에서 3번째행 빙고 맞췄을 땐 인식하지 못함
 *	-> 221208 해결
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	Scanner scan = new Scanner(System.in);
	Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
	
    Container frame = this.getContentPane();    // 안보이면 -> frame에 붙였는지 나중에 확인!
    private static CardLayout cl = new CardLayout();
    static JPanel panel;
	JPanel startScreen;
	JPanel statisticScreen;
	JPanel gameScreen;
    JTextPane txtPane;
    FileManager fm = new FileManager();
    
    GameFrame gf;
    
    // 관리자 모드 - 키면 디버깅 용이함
    public static final boolean adminMode = false;
    
    MainFrame(String title){
        super(title);
        this.setSize(1000, 450);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);   // 바탕화면 중앙에 위치시킴
        init();
        this.setVisible(true);
    }

    public static void main(String[] args){
		new MainFrame("<202211397 한성준> - Bingo Game 만들기");
	}
    
    private void init() {
    	Toolkit kit = Toolkit.getDefaultToolkit();
        
        // 아이콘 설정
        Image img = kit.getImage("BINGO.png");
        this.setIconImage(img);
        frame.setLayout(new BorderLayout());	// 컨테이너 레이아웃 설정 -> BorderLayout
        
        // panel 설정
        panel = new JPanel(cl);
        
        // result.txt file check
        checkFiles();
        
        // word, result 파일 관련 설정
        initFiles();        
        
        // panel에 붙는 screen 설정
        // startScreen 설정
        startScreen = new JPanel();
        initStartScreen();
        
        // statisticScreen 설정
        statisticScreen = new JPanel(new FlowLayout(FlowLayout.CENTER));
        initStatisticScreen();
        
        // gameScreen 설정
        gameScreen = new JPanel();
        initGameScreen();
        
        // 시작 화면 보이게
        cl.show(panel, "startScreen");
        frame.add(panel, BorderLayout.CENTER);
    }

	// result.txt 있는지 체크하고, 없으면 만들어주는 메소드
    private void checkFiles() {
    	// result.txt 파일 체크
    	try {
			File f = new File("result.txt");
			try {
				// result.txt 없으면 생성
				if(!f.exists()){
					System.out.println("result 파일을 새로 생성합니다.");
					f.createNewFile();
	        		
	        		// Writer 생성
	        		FileWriter fWriter = new FileWriter(f);
	        		PrintWriter writer = new PrintWriter(fWriter);
	        		
	        		// 파일 쓰기
	        		writer.write(
	        				"runCnt:" + "0" + "\n"
	        				+ "gameCnt:" + "0" + "\n"
							+ "userWinCnt:" + "3.0" + "\n"
	        				+ "comWinCnt:" + "0");
	        		writer.close();
				}
			}catch(Exception e) {
				System.out.println("Error - result.txt file create error");
				System.out.println(e);
			}
		}catch(Exception e){
			System.out.println("Error - result.txt file checking error");
			System.out.println(e);
		}
	}
    // 단어장, result 파일 선택해주는 메소드
	private void initFiles() {
		if(!adminMode) {
			JOptionPane.showMessageDialog(null, "Welcome to Bingo Game!\n간단한 설정 후 게임이 시작됩니다!", "Alert", JOptionPane.INFORMATION_MESSAGE);
			JOptionPane.showMessageDialog(null, "사용하실 단어장 파일을 선택해 주세요.\n"
					+ "단어장 파일을 선택하지 않으면 게임이 실행되지 않습니다.", "Notice", JOptionPane.INFORMATION_MESSAGE);	    	
		}
		// 단어장 파일 읽음
		fm.readFile(1);
    	FileManager.setWordNum(fm.getWordLst().size());
    	System.out.println("단어장 크기 : " + fm.getWordNum());
    	
    	// 승률 파일 읽음    	
    	fm.readFile(2);
    	FileManager.setRunCnt(FileManager.getRunCnt() + 1);
    	fm.updateStatistics();
    	
    	if(FileManager.getRunCnt() == 1) {
    		message("처음이시군요? 빙고 게임에 오신 걸 환영합니다!", JOptionPane.INFORMATION_MESSAGE);
    	}
    	
    	if(adminMode) {
    		System.out.println("result.txt 갱신 완료");
    		fm.printMembers();
    	}
	}

	private void initGameScreen() {
		JLabel gameScreenText = new JLabel();
		gameScreenText.setText("Game Running");
		gameScreenText.setFont(new Font("Arial", Font.PLAIN, 30));
		
		gameScreen.add(gameScreenText);
		panel.add(gameScreen, "gameScreen");
	}
	
	// 통계 보여주는 화면
    private void initStatisticScreen() {
    	JPanel upper, lower;
    	upper = new JPanel();
    	lower = new JPanel();
    	
    	upper.setPreferredSize(new Dimension(screenSize.width, 100));
    	lower.setPreferredSize(new Dimension(screenSize.width, screenSize.height - 100));
    	
//    	statisticScreen.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 100));
    	statisticScreen.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    	// JOptionPane.showMessageDialog(this, "Game Start!", "Alert", JOptionPane.INFORMATION_MESSAGE);
    	// 전환 버튼 설정
        JButton changeScreenBtn = new JButton("메인화면으로 돌아가기");
        
        txtPane = new JTextPane();
        txtPane.setEditable(false);
        
        // 버튼 액션
        changeScreenBtn.addActionListener(e-> {
//        	cl.next(panel);	// 화면 전환
        	cl.show(panel, "startScreen");	// 버튼 화면전환
        });	
        
        // 통게화면 - upper, lower panel에 화면전환버튼, 통계 텍스트 붙임 
        upper.add(changeScreenBtn);
        lower.add(txtPane);
        
        // upper, lower 통계화면에 붙임
        statisticScreen.add(upper);
        statisticScreen.add(lower);
        
        // 통계화면 panel에 붙임, card view
        panel.add(statisticScreen, "statisticScreen");
    }

	private void initStartScreen() {    	
		JPanel menuButtons = new JPanel(), top, mid, bottom;
		top = new JPanel();
		mid = new JPanel();
		bottom = new JPanel();
		
		top.setPreferredSize(new Dimension(screenSize.width, 60));
		mid.setPreferredSize(new Dimension(screenSize.width, 200));
		bottom.setPreferredSize(new Dimension(screenSize.width, 100));
		
		JButton info = new JButton("게임 정보");
        JButton start = new JButton("게임 시작");
        JButton exit = new JButton("게임 종료");
        
        JButton changeFileBtn = new JButton("단어장 파일 선택");
        // 전환 버튼 설정
        JButton changeScreenBtn = new JButton("승률 보기");
        changeScreenBtn.addActionListener(e-> {
        	fm.readWinningRate("result.txt");
        	final String newStatistic = "통계 현황 \n-\n"
					+ "총 플레이 게임 수 - " + FileManager.getGameCnt() + "\n"
					+ "user가 이긴 횟수 - " + FileManager.getUserWinCnt() + "\n"
					+ "com이 이긴 횟수 - " + FileManager.getComWinCnt() + "\n"
					+ "user 승률 - " + (int)(FileManager.getUserWinRate() * 10000) / (double)100+ "%\n"
        			+ "com 승률 - " + (int)(FileManager.getComWinRate() * 10000) / (double)100+ "%\n";
        	txtPane.setText(newStatistic);
        	revalidate();
        	repaint();
        	
        	cl.show(panel, "statisticScreen");	// 화면 전환
        });	// 화면 전환
        
        // 단어장 파일 이름 저장하는 라벨
        JLabel wordFileNameLabel = new JLabel();
        if(FileManager.getWordFileName().length() < 1) {
        	wordFileNameLabel.setText("선택된 단어장 파일이 없습니다.");
        }else {
        	wordFileNameLabel.setText("선택된 단어장 파일 - " + FileManager.getWordFileName());
        }
        
        
        // 게임 정보 버튼
        info.addActionListener(e->{
        	JOptionPane.showMessageDialog(this, "제작자 : 한성준\n"
        			+ "게임 버전 : 1.0v\n"
        			+ "제작 프로그램 : Java, Swing\n"
        			+ "제작 날짜 : 2022-12-04~ 2022-12-08\n"
        			+ "실행 환경 : \n"
        			+ "Microsoft Windows [Version 10.0.22000.1098]\r\n"
        			+ "(c) Microsoft Corporation. All rights reserved.\r\n"
        			+ "\r\n"
        			+ "java --version : \n"
        			+ "java 18.0.2.1 2022-08-18\r\n"
        			+ "Java(TM) SE Runtime Environment (build 18.0.2.1+1-1)\r\n"
        			+ "Java HotSpot(TM) 64-Bit Server VM (build 18.0.2.1+1-1, mixed mode, sharing)", "게임 정보", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // 게임 시작 버튼
        start.addActionListener(e->{
        	if(FileManager.getWordFileName().length() < 1) {
        		JOptionPane.showMessageDialog(this, "단어장 파일이 선택되지 않았습니다.\n"
        				+ "단어장 파일을 선택 후 게임을 시작해 주세요.", "Notice", JOptionPane.ERROR_MESSAGE);
        	}else {
        		// 먼저 n 입력받고, 결정함
        		String n = null;
    			try {
    				n = JOptionPane.showInputDialog("빙고판 크기 입력 : ");
    				if(n != null) {
	    				if(Integer.parseInt(n) > 1) {
	        				if(fm.getWordNum() < Integer.parseInt(n) * Integer.parseInt(n)) {
	                			message("n이 단어장의 크기보다 큽니다.\n" + "n을 " + ((int)Math.sqrt(fm.getWordNum())) + "이하로 설정해주세요.", JOptionPane.WARNING_MESSAGE);
	                		}else {
	                			// game start
	                			cl.show(panel, "gameScreen");
	                			gf = new GameFrame("Bingo Game - 202211397 한성준", Integer.parseInt(n));
	                		}	
	        			}
	    				else{
							message("n은 1보다 큰 정수로 입력해 주세요.", JOptionPane.INFORMATION_MESSAGE);
						}
    				}
    			}catch(NumberFormatException e1) {	// string 입력 처리
    				message("n은 2 이상의 정수를 입력해 주세요.", JOptionPane.ERROR_MESSAGE);
//    			}catch(Exception e2) {
//    				System.out.println("Error - n 입력 에러\n" + e2);
    			}
        	}
        });
        
        // 게임 종료 버튼
        exit.addActionListener(e->{
        	String[] answer = {"예", "아니오", "취소"}; 
        	int userInput = JOptionPane.showOptionDialog(this, "게임을 종료할까요?", "Notice", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, answer, null);
        	if(userInput == 0) {
        		System.exit(0);
        	}
        });
        
        // 단어장 파일 선택하는 버튼
        changeFileBtn.addActionListener(e->{
        	fm.readFile(1);
        	final String wordFileName = FileManager.getWordFileName();
        	if(FileManager.getWordFileName().length() < 1) {
            	wordFileNameLabel.setText("선택된 단어장 파일이 없습니다.");
            }else {
            	wordFileNameLabel.setText("선택된 단어장 파일 - " + wordFileName);
            }
        	startScreen.revalidate();
        	startScreen.repaint();
        });
        
        // menuButtons 패널에 info, start, exit 버튼 추가
        menuButtons.add(info);
        menuButtons.add(start);
        menuButtons.add(exit);
        
        // startScreen에 menuButtons, changeFileBtn, changeScreenBtn 추가
//        startScreen.add(menuButtons, BorderLayout.NORTH);
//        startScreen.add(changeFileBtn);
//        startScreen.add(changeScreenBtn, BorderLayout.NORTH);
        
        top.add(menuButtons);
        top.add(changeFileBtn);
        top.add(changeScreenBtn);
        
        // 배경 설정
        ImageIcon imgIcon = new ImageIcon("image/BINGO.png");
        JLabel backgroundImgJLabel = new JLabel(imgIcon);
//        startScreen.add(backgroundImgJLabel);
        mid.add(backgroundImgJLabel);
        
        // 현재 단어장 파일 이름 표시 라벨 추가
//        startScreen.add(wordFileNameLabel);
        bottom.add(wordFileNameLabel);
        
        startScreen.add(top, BorderLayout.NORTH);
        startScreen.add(mid);
        startScreen.add(bottom, BorderLayout.NORTH);
        
        panel.add(startScreen, "startScreen");
    }

	// message dialog 간소화
	public static void message(String msg, int msgType) {
		JOptionPane.showMessageDialog(null, msg, "Notice", msgType);
	}
	
	// getter, setter
	
	public static void setCardLayout(String name) {
		cl.show(panel, name);
	}
}