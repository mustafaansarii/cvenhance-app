import { Field, PeriodField, PhotoField } from '../shared';
import { Bullets } from './_parts';

const accent = 'text-[color:var(--rb-accent)]';

const Icon = ({ d }) => (
    <svg viewBox="0 0 24 24" fill="currentColor" className="inline-block h-[1.15em] w-[1.15em] shrink-0 align-text-bottom"><path d={d} /></svg>
);
const ICONS = {
    phone: 'M6.6 10.8c1.4 2.8 3.8 5.1 6.6 6.6l2.2-2.2c.3-.3.7-.4 1-.2 1.1.4 2.3.6 3.6.6.6 0 1 .4 1 1V20c0 .6-.4 1-1 1C10.6 21 3 13.4 3 4c0-.6.4-1 1-1h3.5c.6 0 1 .4 1 1 0 1.2.2 2.4.6 3.6.1.4 0 .8-.3 1l-2.2 2.2z',
    mail: 'M3 5h18a1 1 0 011 1v12a1 1 0 01-1 1H3a1 1 0 01-1-1V6a1 1 0 011-1zm9 7L4 7v1l8 5 8-5V7l-8 5z',
    pin: 'M12 2a7 7 0 00-7 7c0 5 7 13 7 13s7-8 7-13a7 7 0 00-7-7zm0 9.5A2.5 2.5 0 1112 6a2.5 2.5 0 010 5.5z',
    globe: 'M2 5.5A1.5 1.5 0 013.5 4h17A1.5 1.5 0 0122 5.5v13a1.5 1.5 0 01-1.5 1.5h-17A1.5 1.5 0 012 18.5v-13zM4 8v10h16V8H4zm2 2h8v2H6v-2z',
    calendar: 'M19 4h-1V2h-2v2H8V2H6v2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10zm0-12H5V6h14v2z',
};

const ContactItem = ({ icon, value, onChange, ph }) => (
    <div className="flex items-center gap-1.5 text-[0.85em] font-bold text-slate-700">
        <span className={accent}><Icon d={ICONS[icon]} /></span>
        <Field value={value} onChange={onChange} ph={ph} className="min-w-0 flex-1 break-words" />
    </div>
);

const getLangLevel = (val) => {
    const s = String(val || '').toLowerCase();
    if (s.includes('native') || s.includes('bilingual')) return 5;
    if (s.includes('fluent') || s.includes('advanced')) return 4;
    if (s.includes('intermediate') || s.includes('conversational')) return 3;
    if (s.includes('basic') || s.includes('beginner')) return 2;
    const n = parseInt(s.replace(/[^0-9]/g, ''), 10);
    return Math.max(0, Math.min(5, isNaN(n) ? 4 : Math.ceil((n / 100) * 5)));
};

const elegant = {
    code: 'elegant',
    name: 'Elegant',
    accent: '#0066cc',
    sheetClass: 'font-sans text-slate-800',
    layout: {
        type: 'two-column',
        sidebarSide: 'right',
        splitHeader: false,
        sidebar: ['achievements', 'skills', 'education', 'courses', 'interests', 'awards'],
        sidebarWidth: '36%',
        sidebarClass: 'pl-6',
        gap: 'gap-7',
    },

    renderHeader: (r, set) => (
        <div className="mb-4">
            <Field value={r.name} onChange={(v) => set('name', v)} ph="YOUR NAME" className="block text-[3em] font-extrabold uppercase leading-none tracking-tight text-black" />
            <Field value={r.title} onChange={(v) => set('title', v)} ph="The role you are applying for?" className={`mt-2 block text-[1.05em] font-bold ${accent}`} />
            <div className="mt-2 flex flex-wrap items-center gap-x-5 gap-y-2">
                <ContactItem icon="phone" value={r.phone} onChange={(v) => set('phone', v)} ph="Phone" />
                <ContactItem icon="mail" value={r.email} onChange={(v) => set('email', v)} ph="Email" />
                <ContactItem icon="globe" value={r.linkedin} onChange={(v) => set('linkedin', v)} ph="Website / LinkedIn" />
                <ContactItem icon="pin" value={r.location} onChange={(v) => set('location', v)} ph="Address" />
            </div>
        </div>
    ),

    renderTitle: (title) => {
        if (String(title || '').toLowerCase() === 'achievements') return null;
        return (
            <div className="mb-4 border-b-2 border-black pb-1.5">
                <h2 className="text-[1.05em] font-bold uppercase tracking-wider text-black">{title}</h2>
            </div>
        );
    },

    renderText: (value, onChange, ph) => (
        <Field as="p" value={value} onChange={onChange} ph={ph} className="leading-relaxed text-[0.95em] text-slate-700" />
    ),

    renderItem: (kind, ctx) => {
        const { item, update, bullets, primaryPh, secondaryPh, ph, type } = ctx;

        if (kind === 'exp') {
            return (
                <div className="mb-4 border-b border-dotted border-slate-300 pb-4 last:border-0 last:pb-0">
                    <Field value={item.secondary} onChange={(v) => update({ secondary: v })} ph={secondaryPh || 'Role'} className="block text-[1.05em] font-bold text-black" />
                    <Field value={item.primary} onChange={(v) => update({ primary: v })} ph={primaryPh || 'Company'} className={`block text-[1em] font-bold ${accent}`} />
                    <div className="mt-1 flex flex-wrap items-center gap-x-4 gap-y-1 text-[0.8em] font-semibold uppercase tracking-wide text-slate-500">
                        <div className="flex items-center gap-1.5">
                            <Icon d={ICONS.calendar} />
                            <PeriodField value={item.period} onChange={(v) => update({ period: v })} />
                        </div>
                        <div className="flex items-center gap-1.5">
                            <Icon d={ICONS.pin} />
                            <Field value={item.location} onChange={(v) => update({ location: v })} ph="Location" />
                        </div>
                    </div>
                    <div className="mt-2 text-[0.95em] text-slate-700 leading-relaxed">
                        <Bullets bullets={bullets} />
                    </div>
                </div>
            );
        }

        if (kind === 'edu') {
            return (
                <div className="mb-4 border-b border-dotted border-slate-300 pb-4 last:border-0 last:pb-0">
                    <Field value={item.degree} onChange={(v) => update({ degree: v })} ph="Degree and field of study" className="block text-[1em] font-bold text-black" />
                    <Field value={item.school} onChange={(v) => update({ school: v })} ph="School / University" className={`block text-[0.95em] font-bold ${accent}`} />
                    <div className="mt-1 flex flex-wrap items-center gap-x-4 gap-y-1 text-[0.8em] font-semibold text-slate-500">
                        <div className="flex items-center gap-1.5">
                            <Icon d={ICONS.calendar} />
                            <PeriodField value={item.period} onChange={(v) => update({ period: v })} />
                        </div>
                        <div className="flex items-center gap-1.5">
                            <Icon d={ICONS.pin} />
                            <Field value={item.location} onChange={(v) => update({ location: v })} ph="Location" />
                        </div>
                    </div>
                </div>
            );
        }

        if (kind === 'proj') {
            return (
                <div className="mb-4 border-b border-dotted border-slate-300 pb-4 last:border-0 last:pb-0">
                    <Field value={item.primary} onChange={(v) => update({ primary: v })} ph={primaryPh} className="block text-[1.05em] font-bold text-black" />
                    <Field value={item.secondary} onChange={(v) => update({ secondary: v })} ph={secondaryPh} className={`block text-[0.95em] font-bold ${accent}`} />
                    <div className="mt-1 flex flex-wrap items-center gap-3 text-[0.85em] font-semibold text-slate-500">
                        <Field value={item.githubUrl} onChange={(v) => update({ githubUrl: v })} ph="GitHub" className={`underline hover:text-[color:var(--rb-accent)]`} />
                        <Field value={item.liveUrl} onChange={(v) => update({ liveUrl: v })} ph="Live URL" className={`underline hover:text-[color:var(--rb-accent)]`} />
                    </div>
                    <div className="mt-2 text-[0.95em] text-slate-700 leading-relaxed">
                        <Bullets bullets={bullets} />
                    </div>
                </div>
            );
        }

        if (kind === 'pair') {
            if (type === 'languages') {
                const pct = getLangLevel(item.value);
                return (
                    <div className="mb-2 flex items-center justify-between gap-4">
                        <div className="flex min-w-0 flex-col">
                            <Field value={item.label} onChange={(v) => update({ label: v })} ph="Language" className="truncate font-bold text-black" />
                            <Field value={item.value} onChange={(v) => update({ value: v })} ph="Proficiency" className="truncate text-[0.85em] text-slate-500" />
                        </div>
                        <div className="flex shrink-0 gap-[3px]">
                            {[1, 2, 3, 4, 5].map(i => (
                                <div key={i} className={`h-4 w-[6px] rounded-[1px] ${i <= pct ? 'bg-[color:var(--rb-accent)]' : 'bg-slate-200'}`} />
                            ))}
                        </div>
                    </div>
                );
            }
            return (
                <div className="mb-1 text-[0.95em] text-slate-700">
                    <span className="font-bold text-black"><Field value={item.label} onChange={(v) => update({ label: v })} ph="Label" /></span>
                    {item.label && item.value && <span>: </span>}
                    <Field value={item.value} onChange={(v) => update({ value: v })} ph="Value" />
                </div>
            );
        }

        if (kind === 'courses') {
            return (
                <div className="mb-4 border-b border-dotted border-slate-300 pb-4 last:border-0 last:pb-0">
                    <Field value={item.title} onChange={(v) => update({ title: v })} ph={primaryPh} className="block text-[1em] font-bold text-black" />
                    <Field value={item.issuer} onChange={(v) => update({ issuer: v })} ph={secondaryPh} className="mt-1 block text-[0.95em] text-slate-600 leading-relaxed" />
                </div>
            );
        }
        
        if (type === 'achievements' || type === 'awards') {
            return (
                <div className="mb-4 border-b border-dotted border-slate-300 pb-4 last:border-0 last:pb-0">
                    <Field as="p" value={item.text} onChange={(v) => update({ text: v })} ph={ph} className="text-[0.95em] leading-relaxed text-slate-600" />
                </div>
            );
        }

        return (
            <div className="mb-4 border-b border-dotted border-slate-300 pb-4 last:border-0 last:pb-0">
                <Field value={item.primary} onChange={(v) => update({ primary: v })} ph="Title (Optional)" className="mb-1 block text-[1em] font-bold text-black" />
                <Field as="p" value={item.text} onChange={(v) => update({ text: v })} ph={ph} className="text-[0.95em] leading-relaxed text-slate-600" />
            </div>
        );
    },
};

export default elegant;
