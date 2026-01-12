import i18nData from "./i18n.json";
import Lang from "./LangEnum";

/**
 * Returns translation for a key.
 * @param lang The selected language.
 * @param key The key for message.
 * @returns The translated message.
 */
export const getTranslation = (lang: Lang, key: string) => {
    const translations = i18nData.translations as Record<string, Record<string, string>>;
    
    if (!translations || !translations[key]) {
        return key;
    }

    const langStr = lang.toString();
    return translations[key][langStr] || key;
}