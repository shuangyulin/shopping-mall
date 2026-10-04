export function money(value) {
  return `¥${Number(value || 0).toFixed(2)}`
}

export function datetime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ')
}
