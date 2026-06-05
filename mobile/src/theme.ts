import { StyleSheet } from 'react-native';

export const colors = {
  primary: '#1f6f43',
  primaryDark: '#155233',
  bg: '#f5f5f0',
  card: '#ffffff',
  text: '#1c1c1c',
  muted: '#6b6b6b',
  correct: '#1f6f43',
  present: '#c9a227',
  absent: '#9aa0a6',
  danger: '#b00020',
  border: '#d8d8d2',
};

export const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.bg, padding: 16 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: colors.bg },
  title: { fontSize: 22, fontWeight: '700', color: colors.text, marginBottom: 12 },
  subtitle: { fontSize: 15, color: colors.muted, marginBottom: 16 },
  card: {
    backgroundColor: colors.card,
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  button: {
    backgroundColor: colors.primary,
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 10,
  },
  buttonText: { color: '#fff', fontSize: 16, fontWeight: '600' },
  input: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 10,
    padding: 12,
    backgroundColor: '#fff',
    marginBottom: 10,
    fontSize: 16,
    textAlign: 'center',
  },
  link: { color: colors.primary, textAlign: 'center', marginTop: 14, fontSize: 15 },
  error: { color: colors.danger, marginBottom: 10 },
});
