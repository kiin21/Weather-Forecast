# Frontend Module Structure

Dự án đã được chia thành các module nhỏ để dễ quản lý và maintain:

## 📁 Cấu trúc thư mục

```
WorldCast-Frontend/
├── index.ts                    # Entry point - import main module
├── src/
│   ├── main.ts                # Main application logic
│   ├── types/
│   │   └── weather.ts         # Type definitions
│   ├── services/
│   │   └── weatherService.ts  # API service functions
│   ├── components/
│   │   ├── LoadingAnimation.ts    # Loading animation component
│   │   ├── WeatherComponents.ts   # Weather UI components
│   │   └── ResizablePanel.ts      # Resizable panel functionality
│   └── utils/
│       └── textUtils.ts       # Utility functions
├── style.css
├── index.html
└── package.json
```

## 📋 Mô tả từng module

### **index.ts** (Entry Point)

- Import và khởi tạo main module
- Điểm bắt đầu của ứng dụng

### **src/main.ts** (Main Application)

- Logic chính của Google Maps
- Geocoding functionality
- Event handlers
- Global window functions

### **src/types/weather.ts** (Type Definitions)

- `WeatherData` interface
- `ForecastDay` interface
- Tất cả type definitions cho weather data

### **src/services/weatherService.ts** (API Service)

- `fetchWeatherData()` - Gọi API backend
- `checkCompleteResponse()` - Kiểm tra response đầy đủ
- Xử lý HTTP requests và validation

### **src/components/LoadingAnimation.ts** (Loading Component)

- `createLoadingAnimation()` - Tạo loading spinner
- `updateLoadingMessage()` - Cập nhật loading messages
- Loading states management

### **src/components/WeatherComponents.ts** (Weather UI)

- `createCurrentWeatherComponent()` - Current weather display
- `createForecastComponent()` - 7-day forecast display
- Weather icons mapping

### **src/components/ResizablePanel.ts** (Resizable Panel)

- `initResizablePanel()` - Khởi tạo resize functionality
- Mouse event handlers
- Panel resize logic

### **src/utils/textUtils.ts** (Utilities)

- `createExpandableText()` - Show more/less functionality
- `toggleDescription()` - Toggle text expansion
- `sleep()` - Utility function

## 🔧 Cách sử dụng

### **Import modules:**

```typescript
import { WeatherData } from "./types/weather.js";
import { fetchWeatherData } from "./services/weatherService.js";
import { createLoadingAnimation } from "./components/LoadingAnimation.js";
```

### **Sử dụng trong HTML:**

```html
<script
  type="module"
  src="./index.ts"
></script>
```

## ✅ Lợi ích của việc chia module

1. **Maintainability**: Dễ bảo trì và debug
2. **Reusability**: Có thể tái sử dụng components
3. **Separation of Concerns**: Mỗi file có trách nhiệm riêng
4. **Type Safety**: TypeScript types được tổ chức tốt
5. **Testing**: Dễ dàng test từng module riêng biệt
6. **Collaboration**: Nhiều developer có thể làm việc song song

## 🔄 Migration từ file cũ

File `index.ts` cũ đã được thay thế bằng:

- Main logic → `src/main.ts`
- Types → `src/types/weather.ts`
- API calls → `src/services/weatherService.ts`
- UI components → `src/components/`
- Utilities → `src/utils/`

Ứng dụng vẫn hoạt động như cũ nhưng code đã được tổ chức tốt hơn!
