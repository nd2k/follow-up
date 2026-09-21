import type { BabyResponse } from "#lib/types/baby.ts";
import { listBabies } from "#lib/services/babyService.ts";

const CACHE_KEY = "allaitement_babies_cache";
const SELECTED_KEY = "allaitement_selected_baby_id";

let babies = $state<BabyResponse[]>(
  JSON.parse(localStorage.getItem(CACHE_KEY) ?? "[]")
);
let selectedId = $state<number | null>(
  localStorage.getItem(SELECTED_KEY) ? Number(localStorage.getItem(SELECTED_KEY)) : null
);

export const babyStore = {
  get babies() { return babies; },
  get selectedId() { return selectedId; },
  get selected() { return babies.find((b) => b.id === selectedId) ?? null; },

  select(id: number) {
    selectedId = id;
    localStorage.setItem(SELECTED_KEY, String(id));
  },

  async refresh() {
    const fresh = await listBabies();
    babies = fresh;
    localStorage.setItem(CACHE_KEY, JSON.stringify(fresh));
    if ((!selectedId || !fresh.some((b) => b.id === selectedId)) && fresh.length > 0) {
      this.select(fresh[0].id);
    }
  },
};