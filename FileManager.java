package java_bingo_game;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileManager{	// Word파일, result.txt파일 관리하는 클래스
    // Members
	// 단어장 파일 관련 멤버변수들
	private String fileName = "quiz.txt";
	private static String wordFileName = "";
    private static int wordNum = 0;
    private static ArrayList<Word> wordLst = new ArrayList<>();
    
    // 승률 파일 관련 멤버변수들
    private static int runCnt;	// 실행횟수 저장
    private static int gameCnt;	// 게임 플레이 횟수 저장
    private static int userWinCnt;	// 사용자가 게임 이긴 횟수 저장
    private static int comWinCnt;
    
    private static double userWinRate;
    private static double comWinRate;
    // Constructor
    FileManager(){  }

    // Methods

    // 단어 파일 읽어 인자로 받은 ArrayList<Word> wlst에 저장하는 메소드
    // 1이면 word, 2면 word
    public void readFile(int fileType) {
    	String filePath = "";
    	while(true) {
	    	try {
	    		// 단어장 파일 읽어올 때
				if(fileType == 1) {
					if(MainFrame.adminMode) {	// 관리자 모드일때는 파일 하나로 고정해서 선택하는 부분 제거
						// 미리 읽을 파일 이름 설정
//		    			fileName = "16words.txt";
						setWordFileName(fileName);
						readWordFile(fileName);
		    		}else {
		    			// JFileChooser 실행시켜 파일 선택함
		    			JFileChooser fc = new JFileChooser();
						
						// 파일 열 기본 경로 설정
				    	String currentFolderPath = System.getProperty("user.dir");
				    	fc.setCurrentDirectory(new File(currentFolderPath));
				    	
				    	// 필터 설정
				    	FileNameExtensionFilter filter = new FileNameExtensionFilter("txt Files",  "txt");
				    	fc.setFileFilter(filter);
						
				    	// 파일 선택창 열기	
			    		int returnVal = fc.showOpenDialog(null);
			    		// 확인 눌렀을 때
			    		if(returnVal == JFileChooser.APPROVE_OPTION) {
			    			filePath = fc.getSelectedFile().getPath();
			    			setWordFileName(fc.getSelectedFile().getName());
			    			readWordFile(filePath);
			    		}
		    		}
		    		break;	// 제대로 오류 안나고 선택되었을때만 빠져나감
				}else {
					// 승률 파일 읽어올 때
					readWinningRate("result.txt");
					break;
				}
			}catch(NoSuchElementException e) {
				setWordFileName("");
				System.out.println("Error - readFile 메소드 에러 - No file\n" + e);
				JOptionPane.showMessageDialog(null, "파일 형식이 다릅니다.\n다시 텍스트 파일을 선택해 주세요!", "Notice", JOptionPane.ERROR_MESSAGE);
			}catch(Exception e) {
				setWordFileName("");
				// System.out.println("Error - readFile 메소드 에러 - 형식 Error\n" + e);
				JOptionPane.showMessageDialog(null, "파일 형식이 다릅니다.\n다시 텍스트 파일을 선택해 주세요!", "Notice", JOptionPane.ERROR_MESSAGE);
			}
    	}
    }
    
    public void readWordFile(String fileName) {
    	if(fileName == null) {
    		System.out.println("파일 이름이 null값입니다.");
    		return;
    	}
//    	System.out.println("단어장 파일 이름 - " + fileName);
    	wordNum = 0;
    	try(Scanner scan = new Scanner(new File(fileName), "UTF-8")){
        	while(scan.hasNextLine()) {
                String str = scan.nextLine();
                String[] temp = str.split("\t");
                wordLst.add(new Word(temp[0].trim(), temp[1].trim()));
//                System.out.println(str);
            }
            // 출력 테스트
        	wordNum = wordLst.size();
        }catch(FileNotFoundException e) {
            System.out.println("단어 파일 읽기에 실패했습니다. 파일 경로를 다시 확인해주세요 (java project 폴더 아래에 위치)");
            System.out.println(e);
            System.out.println(System.getProperty("user.dir"));
        }
    }
    
	public void readWinningRate(String fileName){
        try(Scanner scan = new Scanner(new File(fileName), "UTF-8")){
        	while(scan.hasNextLine()) {
            	String str = scan.nextLine();
            	// System.out.println("str - " + str); // str 테스트용 print
                String[] line = str.split(":");
                if(line.length == 2) {
                	switch(line[0]) {
                	case "runCnt":
                		FileManager.runCnt = Integer.parseInt(line[1]);
                		break;
                	case "gameCnt":
                		FileManager.gameCnt = Integer.parseInt(line[1]);
                		break;
                	case "userWinCnt":
                		FileManager.userWinCnt = Integer.parseInt(line[1]);
                		break;
                	case "comWinCnt":
                		FileManager.comWinCnt = Integer.parseInt(line[1]);
                		break;
                	}
                }
            }
        	// 이긴 횟
        	if(userWinCnt + comWinCnt == 0) {
        		userWinRate = 0;
        		comWinRate = 0;
        	}else {
        		userWinRate = userWinCnt / (double)(userWinCnt + comWinCnt);
        		comWinRate = 1 - userWinRate;
        	}
        	
        }catch(Exception e) {
            System.out.println("승률 파일 읽기에 실패했습니다. 파일 경로를 다시 확인해주세요 (java project 폴더 아래에 위치)");
            System.out.println(e);
            System.out.println(System.getProperty("user.dir"));
        }
    }
	
	public void updateStatistics() {
		// 파일에 runCnt 정보 업데이트
    	try {
    		File file = new File("result.txt");
    		// Writer 생성
    		FileWriter fWriter = new FileWriter(file);
    		PrintWriter writer = new PrintWriter(fWriter);
    		
    		userWinRate = userWinCnt / (double)(userWinCnt + comWinCnt);
    		comWinRate = 1 - userWinRate;
    		
    		// 파일 쓰기
    		writer.write(
    				"runCnt:" + runCnt + "\n"
    				+ "gameCnt:" + gameCnt + "\n"
					+ "userWinCnt:" + userWinCnt + "\n"
    				+ "comWinCnt:" + comWinCnt);
    		writer.close();        		
    	}catch(Exception e) {
    		System.out.println("Error - updateStatistics 에러\n" + e);
    	}
	}


    // 단어 ArrayList 안겹치도록 랜덤하게 n^2개 return하는 메소드
    public Word[][] getRandomWordArray(int n){
    	System.out.println("n - " + n + "\nwordNum - " + wordNum);
        if(n * n > wordNum){    // n * n > 단어개수 이라면
        	JOptionPane.showMessageDialog(null, "단어장에 있는 단어의 개수가 요청하신 것보다 적습니다!", "Notice", JOptionPane.ERROR_MESSAGE);
            System.out.println("단어장에 있는 단어의 개수가 요청하신 것보다 적습니다!");
            return null;
        }
        Random r = new Random();
        HashSet<Word> wordSet = new HashSet<>(wordLst); // 원래 단어 모두 들어있는 set
        Word[][] randomWords = new Word[n][n];

        for(int i = 0; i < n; i ++){    // n^2번 반복해 단어가 남아있는 set에서만 단어 뽑아냄
            for(int j = 0; j < n; j++){
                int idx = r.nextInt(wordSet.size());
                Iterator<Word> it = wordSet.iterator();
                for(int k = 0; k < idx; k++) it.next(); // wordSet의 n번째 요소 접근
                Word w = it.next();
                randomWords[i][j] = w;
                wordSet.remove(w);
            }
        }
        return randomWords;
    }


    // getter, setter
    public void printMembers() {
    	System.out.println("runCnt : " + runCnt);
    	System.out.println("gameCnt : " + gameCnt);
    	System.out.println("userWinCnt : " + userWinCnt);
    	System.out.println("comWinCnt : " + comWinCnt);
    }
    
    public ArrayList<Word> getWordLst() {
        return wordLst;
    }

    public int getWordNum() {
        return wordNum;
    }

    
    // 승률
    public static String getWordFileName() {
		return wordFileName;
	}

	public static void setWordFileName(String wordFileName) {
		FileManager.wordFileName = wordFileName;
	}
    
	public static int getRunCnt() {
		return runCnt;
	}

	public static void setRunCnt(int runCnt) {
		FileManager.runCnt = runCnt;
	}    

	public static int getGameCnt() {
		return gameCnt;
	}
	public static void setGameCnt(int gameCnt) {
		FileManager.gameCnt = gameCnt;
	}

	public static int getUserWinCnt() {
		return userWinCnt;
	}
	
	public static void setUserWinCnt(int userWinCnt) {
		FileManager.userWinCnt = userWinCnt;
	}

	public static int getComWinCnt() {
		return comWinCnt;
	}
	public static void setComWinCnt(int comWinCnt) {
		FileManager.comWinCnt = comWinCnt;
	}

	public static double getUserWinRate() {
		return userWinRate;
	}

	public static double getComWinRate() {
		return comWinRate;
	}

	public static void setWordNum(int wordNum) {
		FileManager.wordNum = wordNum;
	}	
}
