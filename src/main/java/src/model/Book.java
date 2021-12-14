//package src.model;
//
//public class Book {
//    private int bookId;
//    private String bookTitle;
//    private int amount;
//    private int available;
//
//    public int getBookId() {
//        return bookId;
//    }
//
//    public void setBookId(int bookId) {
//        this.bookId = bookId;
//    }
//
//    public String getBookTitle() {
//        return bookTitle;
//    }
//
//    public void setBookTitle(String bookTitle) {
//        this.bookTitle = bookTitle;
//    }
//
//    public int getAmount() {
//        return amount;
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }
//
//    public int getAvailable() {
//        return available;
//    }
//
//    public void setAvailable(int available) {
//        this.available = available;
//    }
//
//    public Book() {
//    }
//
//    public Book(int bookId, String bookTitle, int amount) {
//        this.bookId = bookId;
//        this.bookTitle = bookTitle;
//        this.amount = amount;
//    }
//
//    public void showBookInfo() {
//        System.out.println(this.toString());
//    }
//
//    @Override
//    public String toString() {
//        return " bookId: " + bookId +
//                "\n bookTitle: '" + bookTitle + '\'' +
//                "\n amount: " + amount +
//                "\n available: " + available;
//    }
//    public boolean findBook(String keyword){ Map<Integer,String> findBooks = new HashMap<>(); String[] words = keyword.split("\\s"); for (String word : words) { for (int i = 0; i < bookList.size(); i++) { if (bookList.get(i).getBookTitle().contains(word)); findBooks.put(bookList.get(i).getBookId(),bookList.get(i).getBookTitle()); } } int sizeResult = findBooks.size(); if(sizeResult>0){ System.out.println("Total book find: "+sizeResult); System.out.println(findBooks); return true; } return false; }
//
//    public static void main(String[] args) {
//        Book book = new Book(123, "dfsd",123);
//        book.showBookInfo();
//
//    }
//}
//def exercise_08(number1, number2, number3):
//        # change the line 'result = None' to perform the appropriate calculation
//        if (number1 >= number2 and number1 <= number3) or (number1 >= number3 and number1 <= number2) :
//            return number1
//        if (number2 >= number1 and number2 <= number3) or (number2 >= number3 and number2 <= number1) :
//            return number2
//        return number3