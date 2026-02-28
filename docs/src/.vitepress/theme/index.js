import DefaultTheme from "vitepress/theme";
import { nextTick, onMounted, onUnmounted, watch } from "vue";
import { useRoute } from "vitepress";

let preferredTitle = null;

function getLabelTitle(label) {
  return label.dataset.title;
}

function getSelectedTitle(group) {
  const checked = group.querySelector(".tabs input:checked");
  const label = group.querySelector(`label[for="${checked.id}"]`);
  return getLabelTitle(label);
}

function activateTabByTitle(title, skipGroup = null) {
  for (const group of document.querySelectorAll(".vp-code-group")) {
    if (group === skipGroup) {
      continue;
    }

    const labels = Array.from(group.querySelectorAll(".tabs label"));
    const index = labels.findIndex((label) => getLabelTitle(label) === title);

    if (index < 0) {
      continue;
    }

    const input = group.querySelectorAll(".tabs input")[index];
    if (input.checked) {
      continue;
    }

    input.checked = true;

    const blocks = group.querySelector(".blocks");
    blocks.querySelector(".active")?.classList.remove("active");
    blocks.children[index].classList.add("active");
  }
}

function syncTabs() {
  if (preferredTitle == null) {
    return;
  }

  activateTabByTitle(preferredTitle);
}

export default {
  extends: DefaultTheme,
  setup() {
    const route = useRoute();
    let syncing = false;

    const handleTabActivate = (event) => {
      if (syncing) {
        return;
      }

      const block = event.detail;
      const group = block.closest(".vp-code-group");
      const title = getSelectedTitle(group);

      preferredTitle = title;

      syncing = true;
      activateTabByTitle(title, group);
      syncing = false;
    };

    onMounted(() => {
      syncing = true;
      syncTabs();
      syncing = false;

      window.addEventListener(
        "vitepress:codeGroupTabActivate",
        handleTabActivate,
      );

      watch(
        () => route.path,
        async () => {
          await nextTick();

          syncing = true;
          syncTabs();
          syncing = false;
        },
      );
    });

    onUnmounted(() => {
      window.removeEventListener(
        "vitepress:codeGroupTabActivate",
        handleTabActivate,
      );
    });
  },
};
