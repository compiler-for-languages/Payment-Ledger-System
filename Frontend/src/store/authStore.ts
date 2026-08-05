import { create } from "zustand";
import { authService } from "../services/AuthService";
import { AuthUser, RegisterPayload } from "../types/auth";

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  isHydrated: boolean;

  // Actions
  login: (email: string, password: string) => Promise<void>;
  register: (payload: RegisterPayload) => Promise<void>;
  logout: () => void;
  setAuth: (token: string, user: AuthUser) => void;
  initialize: () => Promise<void>;
}

// Module-level flag: guarantees initialize() runs exactly once
// across StrictMode double-invocation and any other callers.
let _initializeCalled = false;

export const useAuthStore = create<AuthState>((set, get) => ({
  token: null,
  user: null,
  isAuthenticated: false,
  isHydrated: false,

  setAuth: (token, user) => {
    localStorage.setItem("pls.token", token);
    set({ token, user, isAuthenticated: true, isHydrated: true });
  },

  login: async (email, password) => {
    const data = await authService.login(email, password);
    get().setAuth(data.token, data.user);
    // Mark as initialized to prevent duplicate GET /users/me calls
    _initializeCalled = true;
  },

  register: async (payload) => {
    const data = await authService.register(payload);
    get().setAuth(data.token, data.user);
    // Mark as initialized to prevent duplicate GET /users/me calls
    _initializeCalled = true;
  },

  logout: () => {
    localStorage.removeItem("pls.token");
    // Reset the flag so initialize() can run again after logout + re-login
    _initializeCalled = false;
    set({ token: null, user: null, isAuthenticated: false, isHydrated: true });
  },

  initialize: async () => {
    // Guard: only run once per application lifetime.
    // React StrictMode calls effects twice; this flag prevents double GET /users/me.
    if (_initializeCalled) return;
    _initializeCalled = true;

    const token = localStorage.getItem("pls.token");
    if (!token) {
      set({ isHydrated: true });
      return;
    }

    try {
      const user = await authService.getMe();
      set({ token, user, isAuthenticated: true, isHydrated: true });
    } catch (error) {
      console.error("Auth initialization failed", error);
      localStorage.removeItem("pls.token");
      set({ token: null, user: null, isAuthenticated: false, isHydrated: true });
    }
  },
}));
