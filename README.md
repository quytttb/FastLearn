# FastLearn

Ứng dụng học tập trên Android với Jetpack Compose, Room Database và tích hợp Gemini API.

## Mô tả

FastLearn là một ứng dụng Android được thiết kế để hỗ trợ học tập, đặc biệt là ghi chú, tóm tắt và tạo flashcard từ hình ảnh của tài liệu. Ứng dụng tận dụng các công nghệ hiện đại như Jetpack Compose cho giao diện người dùng, Room Database để lưu trữ dữ liệu cục bộ và Gemini API để xử lý hình ảnh và tóm tắt.

## Tính năng chính

*   **Giao diện Compose:** Ứng dụng có giao diện hiện đại và linh hoạt được xây dựng bằng Jetpack Compose.
*   **Quản lý tài liệu:** Chụp ảnh hoặc chọn tài liệu từ thư viện, tổ chức và xem lại chúng.
*   **Tóm tắt thông minh:** Sử dụng API Gemini để tự động tóm tắt nội dung tài liệu.
*   **Tạo Flashcard:** Dễ dàng tạo flashcard từ nội dung tài liệu đã chụp.
*   **Học tập hiệu quả:** Sử dụng flashcard để ôn tập và củng cố kiến thức.
*   **Theo dõi tiến độ:** Theo dõi tiến độ học tập của bạn và xem lại những câu hỏi đã trả lời sai.
*   **Lưu trữ cục bộ:** Dữ liệu được lưu trữ cục bộ trên thiết bị bằng Room Database.

## Cấu trúc dự án

Dự án được tổ chức thành các package rõ ràng, mỗi package chịu trách nhiệm cho một khía cạnh cụ thể của ứng dụng.

*   **`MainActivity.kt`:** Điểm khởi đầu của ứng dụng.
*   `StudyAppApplication.kt`: Khởi tạo cơ sở dữ liệu.
*   `ui/`: Chứa tất cả các composable liên quan đến giao diện người dùng:
*   `ui/theme/`: Định nghĩa các thuộc tính giao diện (màu sắc, kiểu chữ) để đảm bảo tính nhất quán cho ứng dụng.
    *   `ui/components/`: Các composable nhỏ hơn được sử dụng lại trong nhiều màn hình.
    *   `ui/documents/`: Quản lý danh sách và hiển thị tài liệu.
    *   `ui/flashcards/`: Hiển thị và quản lý flashcard.
    *   `ui/capture/`: Chụp ảnh tài liệu và xử lý hình ảnh.
    *   `ui/study/`:  Chứa các màn hình liên quan đến quá trình học tập và ôn luyện.
    *   `ui/profile/`: Hiển thị và chỉnh sửa thông tin hồ sơ người dùng.
*   `domain/`: Chứa các class mô hình dữ liệu (data classes) và các use case.
*   `data/`: Chứa các lớp liên quan đến dữ liệu:
    *   `data/repository/`: Cung cấp các interface cho việc truy xuất dữ liệu từ các nguồn khác nhau.
    *   `data/local/`:  Xử lý lưu trữ dữ liệu cục bộ.
        *   `data/local/dao/`: Data Access Objects (DAO) cho Room Database.
        *   `data/local/entity/`:  Các entity (bảng) trong Room Database.
    *   `data/remote/`:  (Trong trường hợp này không có, nếu sau này muốn dùng API Gemini)
        *   `data/remote/api/`: Chứa interface định nghĩa các endpoints của Gemini API.
        *   `data/remote/util/`: Chứa Retrofit, helper.
    *   `data/util/`: Chứa các lớp tiện ích để ánh xạ dữ liệu (DataMapper) và xử lý hình ảnh (OcrHelper).
*   `util/`: Chứa các tiện ích khác nhau như xử lý hình ảnh (`ImageUtils`), coroutines (`CoroutineUtils`), và các hằng số (`Constants`).
*   `res/`: Tài nguyên ứng dụng (hình ảnh, bố cục, giá trị, v.v.).
*   `navigation/`: Định nghĩa các tuyến điều hướng trong ứng dụng Compose.

## Thư viện đã sử dụng

*   **Jetpack Compose:** UI
*   **Jetpack Navigation:** Điều hướng
*   **Kotlin Coroutines:** Tác vụ bất đồng bộ
*   **Room Persistence Library:** Lưu trữ cục bộ
*   **Retrofit:** Gọi API
*   **Gemini API:** Xử lý ảnh

## Hướng dẫn cài đặt

1.  Clone repository về máy:
    ```bash
    git clone [địa chỉ repository]
    ```
2.  Mở dự án trong Android Studio.
3.  Đồng bộ và build lại.
4.  Sửa file `local.properties`
    ```text
    GEMINI_API_KEY="YOUR API KEY HERE"
    ```
5.  Chạy ứng dụng trên thiết bị Android hoặc emulator.

## Hướng dẫn sử dụng

*   **HomeScreen:** Chọn một chủ đề để học hoặc tạo một chủ đề mới.
*   **FlashcardScreen:** Xem flashcard, lật thẻ để xem câu hỏi và câu trả lời.
*   **QuizScreen:** Làm bài kiểm tra và xem điểm số trên màn hình kết quả.
*   **DocumentsScreen:** Thêm, xem và quản lý tài liệu.
*   **CaptureScreen:** Chụp ảnh tài liệu.
*   **ProfileScreen:** Xem và chỉnh sửa thông tin cá nhân.

## Lưu ý

*   **API Key:** Do Google mới công bố bản quyền nên API Gemini còn bị giới hạn
IGNORE_WHEN_COPYING_START
content_copy
download
Use code with caution.
IGNORE_WHEN_COPYING_END
