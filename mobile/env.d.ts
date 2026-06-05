// Типизация переменных окружения Expo (EXPO_PUBLIC_*).
// babel-preset-expo инлайнит их в бандл при сборке; здесь только типы для TS.
declare const process: {
  env: {
    EXPO_PUBLIC_API_URL?: string;
    [key: string]: string | undefined;
  };
};
