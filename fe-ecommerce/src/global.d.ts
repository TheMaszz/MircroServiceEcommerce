declare global {
    interface Window {
      alertInfo: (msg: string) => void;
      alertSuccess: (msg: string) => void;
      alertFail: (msg: string) => void;
      alertLoading: () => void;
      closeLoading: () => void;
    }
  }
  
  export {};
  